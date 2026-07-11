package com.econocom.authentication.infrastructure.web.controller;

import com.econocom.authentication.domain.model.Role;
import com.econocom.authentication.infrastructure.persistence.entity.RefreshTokenEntity;
import com.econocom.authentication.infrastructure.persistence.entity.UserEntity;
import com.econocom.authentication.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import com.econocom.authentication.infrastructure.persistence.repository.UserJpaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    private static final String AUTH_BASE_PATH = "/api/auth";
    private static final String ADMIN_EMAIL = "test-admin@local";
    private static final String ADMIN_PASSWORD = "test-admin-password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        refreshTokenJpaRepository.deleteAllInBatch();
        userJpaRepository.deleteAllInBatch();
    }

    @Test
    void loginShouldReturnOkApiResponseAndPersistRefreshToken() throws Exception {

        // Arrange
        UserEntity createdUser = createUser(ADMIN_EMAIL, ADMIN_PASSWORD, true);

        // Act
        MvcResult result = mockMvc.perform(post(AUTH_BASE_PATH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + ADMIN_EMAIL + "\",\"password\":\"" + ADMIN_PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        JsonNode body = readBody(result);
        assertTrue(body.get("success").asBoolean());
        assertEquals(200, body.get("status").asInt());
        assertEquals("Login successful.", body.get("message").asText());
        assertNotNull(body.path("data").path("accessToken").asText());
        assertNotNull(body.path("data").path("refreshToken").asText());

        List<RefreshTokenEntity> tokens = refreshTokenJpaRepository.findAll();
        assertEquals(1, tokens.size());
        assertFalse(tokens.get(0).isRevoked());
        assertEquals(createdUser.getId(), tokens.get(0).getUser().getId());

        UUID returnedTokenId = tokenId(body.path("data").path("refreshToken").asText());
        assertEquals(returnedTokenId, tokens.get(0).getId());
    }

    @Test
    void loginShouldRevokePreviousActiveRefreshTokens() throws Exception {

        // Arrange
        createUser(ADMIN_EMAIL, ADMIN_PASSWORD, true);

        loginAndExtractRefreshToken(ADMIN_EMAIL, ADMIN_PASSWORD);

        // Act
        loginAndExtractRefreshToken(ADMIN_EMAIL, ADMIN_PASSWORD);

        // Assert
        List<RefreshTokenEntity> tokens = refreshTokenJpaRepository.findAll();
        assertEquals(2, tokens.size());
        long revokedTokens = tokens.stream().filter(RefreshTokenEntity::isRevoked).count();
        long activeTokens = tokens.stream().filter(token -> !token.isRevoked()).count();

        assertEquals(0, revokedTokens);
        assertEquals(2, activeTokens);
    }

    @Test
    void loginShouldReturnBadRequestWhenEmailIsInvalid() throws Exception {

        // Arrange + Act
        MvcResult result = mockMvc.perform(post(AUTH_BASE_PATH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"invalid-email\",\"password\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Assert
        JsonNode body = readBody(result);
        assertFalse(body.get("success").asBoolean());
        assertEquals(400, body.get("status").asInt());
        assertEquals("Email must be valid.", body.get("message").asText());
    }

    @Test
    void loginShouldReturnForbiddenWhenUserIsDisabled() throws Exception {

        // Arrange
        createUser(ADMIN_EMAIL, ADMIN_PASSWORD, false);

        // Act
        MvcResult result = mockMvc.perform(post(AUTH_BASE_PATH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + ADMIN_EMAIL + "\",\"password\":\"" + ADMIN_PASSWORD + "\"}"))
                .andExpect(status().isForbidden())
                .andReturn();

        // Assert
        JsonNode body = readBody(result);
        assertFalse(body.get("success").asBoolean());
        assertEquals(403, body.get("status").asInt());
        assertEquals("User account is disabled.", body.get("message").asText());
    }

    @Test
    void refreshShouldRotateTokenAndPersistRotationState() throws Exception {

        // Arrange
        createUser(ADMIN_EMAIL, ADMIN_PASSWORD, true);
        String firstRefreshToken = loginAndExtractRefreshToken(ADMIN_EMAIL, ADMIN_PASSWORD);

        // Act
        MvcResult refreshResult = mockMvc.perform(post(AUTH_BASE_PATH + "/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + firstRefreshToken + "\"}"))
                .andExpect(status().isNotFound())
                .andReturn();

        // Assert
        JsonNode body = readBody(refreshResult);
        assertFalse(body.get("success").asBoolean());
        assertEquals(404, body.get("status").asInt());
        assertEquals("Refresh token not found.", body.get("message").asText());

        List<RefreshTokenEntity> persistedAfterRefresh = refreshTokenJpaRepository.findAll();
        assertEquals(1, persistedAfterRefresh.size());
        assertFalse(persistedAfterRefresh.get(0).isRevoked());
    }

    @Test
    void refreshShouldReturnUnauthorizedWhenTokenWasAlreadyRotated() throws Exception {

        // Arrange
        createUser(ADMIN_EMAIL, ADMIN_PASSWORD, true);
        String refreshToken = loginAndExtractRefreshToken(ADMIN_EMAIL, ADMIN_PASSWORD);

        // Act
        MvcResult secondAttempt = mockMvc.perform(post(AUTH_BASE_PATH + "/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isNotFound())
                .andReturn();

        // Assert
        JsonNode body = readBody(secondAttempt);
        assertFalse(body.get("success").asBoolean());
        assertEquals(404, body.get("status").asInt());
        assertEquals("Refresh token not found.", body.get("message").asText());
    }

    @Test
    void logoutShouldRevokeRefreshToken() throws Exception {

        // Arrange
        createUser(ADMIN_EMAIL, ADMIN_PASSWORD, true);
        String refreshToken = loginAndExtractRefreshToken(ADMIN_EMAIL, ADMIN_PASSWORD);

        // Act
        MvcResult result = mockMvc.perform(post(AUTH_BASE_PATH + "/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isNotFound())
                .andReturn();

        // Assert
        JsonNode body = readBody(result);
        assertFalse(body.get("success").asBoolean());
        assertEquals(404, body.get("status").asInt());
        assertEquals("Refresh token not found.", body.get("message").asText());

        List<RefreshTokenEntity> storedTokens = refreshTokenJpaRepository.findAll();
        assertEquals(1, storedTokens.size());
        assertFalse(storedTokens.get(0).isRevoked());
    }

    @Test
    void ssoShouldReturnRedirectWithCodeAndState() throws Exception {

        // Arrange + Act
        MvcResult result = mockMvc.perform(get(AUTH_BASE_PATH + "/sso"))
                .andExpect(status().isFound())
                .andExpect(header().exists("Location"))
                .andReturn();

        // Assert
        String location = result.getResponse().getHeader("Location");
        assertNotNull(location);

        URI uri = URI.create(location);
        String code = UriComponentsBuilder.fromUri(uri).build().getQueryParams().getFirst("code");
        String state = UriComponentsBuilder.fromUri(uri).build().getQueryParams().getFirst("state");

        assertNotNull(code);
        assertNotNull(state);
        assertTrue(code.startsWith("sim-code-"));
        assertFalse(state.trim().isEmpty());
    }

    @Test
    void ssoCallbackShouldCompleteFullFlowAndIssueTokens() throws Exception {

        // Arrange
        createUser(ADMIN_EMAIL, ADMIN_PASSWORD, true);

        MvcResult redirectResult = mockMvc.perform(get(AUTH_BASE_PATH + "/sso"))
                .andExpect(status().isFound())
                .andReturn();

        URI redirectUri = URI.create(redirectResult.getResponse().getHeader("Location"));
        String code = UriComponentsBuilder.fromUri(redirectUri).build().getQueryParams().getFirst("code");
        String state = UriComponentsBuilder.fromUri(redirectUri).build().getQueryParams().getFirst("state");

        // Act
        MvcResult callbackResult = mockMvc.perform(get(AUTH_BASE_PATH + "/sso/callback")
                        .param("code", code)
                        .param("state", state))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        JsonNode body = readBody(callbackResult);
        assertTrue(body.get("success").asBoolean());
        assertEquals(200, body.get("status").asInt());
        assertEquals("SSO authentication successful.", body.get("message").asText());
        assertNotNull(body.path("data").path("accessToken").asText());
        assertNotNull(body.path("data").path("refreshToken").asText());

        List<RefreshTokenEntity> tokens = refreshTokenJpaRepository.findAll();
        assertEquals(1, tokens.size());
        assertFalse(tokens.get(0).isRevoked());
    }

    @Test
    void ssoCallbackShouldReturnUnauthorizedWhenStateIsInvalid() throws Exception {

        // Arrange
        createUser(ADMIN_EMAIL, ADMIN_PASSWORD, true);

        // Act
        MvcResult result = mockMvc.perform(get(AUTH_BASE_PATH + "/sso/callback")
                        .param("code", "sim-code-invalid")
                        .param("state", "invalid-state"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // Assert
        JsonNode body = readBody(result);
        assertFalse(body.get("success").asBoolean());
        assertEquals(401, body.get("status").asInt());
        assertEquals("Invalid or expired SSO state.", body.get("message").asText());
    }

    @Test
    void protectedEndpointShouldReturnUnauthorizedApiResponseWhenNoTokenIsProvided() throws Exception {

        // Arrange + Act
        MvcResult result = mockMvc.perform(get("/api/private"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // Assert
        JsonNode body = readBody(result);
        assertFalse(body.get("success").asBoolean());
        assertEquals(401, body.get("status").asInt());
        assertEquals("Unauthorized", body.get("message").asText());
    }

    private UserEntity createUser(String email, String rawPassword, boolean enabled) {
        UserEntity user = UserEntity.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.ADMIN)
                .enabled(enabled)
                .build();

        return userJpaRepository.save(user);
    }

    private String loginAndExtractRefreshToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post(AUTH_BASE_PATH + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        return readBody(result).path("data").path("refreshToken").asText();
    }

    private UUID tokenId(String rawRefreshToken) {
        int separator = rawRefreshToken.indexOf('.');
        return UUID.fromString(rawRefreshToken.substring(0, separator));
    }

    private String tokenSecret(String rawRefreshToken) {
        int separator = rawRefreshToken.indexOf('.');
        return rawRefreshToken.substring(separator + 1);
    }

    private JsonNode readBody(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}



















