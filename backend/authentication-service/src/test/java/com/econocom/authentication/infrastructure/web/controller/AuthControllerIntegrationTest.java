package com.econocom.authentication.infrastructure.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.econocom.authentication.infrastructure.persistence.entity.RefreshTokenEntity;
import com.econocom.authentication.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import com.econocom.authentication.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RefreshTokenJpaRepository refreshTokenRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;


    @Test
    void loginShouldRejectInvalidCredentials() throws Exception {
        // Arrange
        UUID userId = findUserIdByEmail(adminEmail);

        // Act
        MvcResult result = login(adminEmail, adminPassword + "-wrong");
        JsonNode body = readBody(result);

        // Assert HTTP
        assertThat(result.getResponse().getStatus()).isEqualTo(401);
        assertThat(body.path("success").asBoolean()).isFalse();
        assertThat(body.path("code").asText()).isNotBlank();
        assertThat(body.path("message").asText()).isNotBlank();
        assertThat(body.path("data").isMissingNode() || body.path("data").isNull()).isTrue();

        // Assert Database
        assertThat(countRefreshTokensByUser(userId)).isEqualTo(0L);
    }


    @Test
    void refreshShouldRejectMalformedRefreshToken() throws Exception {
        // Arrange
        String malformedRefreshToken = "not-a-valid-refresh-token";

        // Act
        MvcResult result = refresh(malformedRefreshToken);
        JsonNode body = readBody(result);

        // Assert HTTP
        assertThat(result.getResponse().getStatus()).isEqualTo(401);
        assertThat(body.path("success").asBoolean()).isFalse();
        assertThat(body.path("code").asText()).isEqualTo("AUTH-004");
        assertThat(body.path("message").asText()).isEqualTo("Invalid refresh token.");
        assertThat(body.path("data").isMissingNode() || body.path("data").isNull()).isTrue();

        // Assert Database
        assertThat(countAllRefreshTokens()).isEqualTo(0L);
    }

    @Test
    void refreshShouldRejectUnknownRefreshToken() throws Exception {
        // Arrange
        UUID unknownTokenId = UUID.randomUUID();
        String unknownRefreshToken = unknownTokenId + ".unknown-secret";

        // Act
        MvcResult result = refresh(unknownRefreshToken);
        JsonNode body = readBody(result);

        // Assert HTTP
        assertThat(result.getResponse().getStatus()).isEqualTo(404);
        assertThat(body.path("success").asBoolean()).isFalse();
        assertThat(body.path("code").asText()).isNotBlank();
        assertThat(body.path("message").asText()).isNotBlank();
        assertThat(body.path("data").isMissingNode() || body.path("data").isNull()).isTrue();

        // Assert Database
        assertThat(findRefreshToken(unknownTokenId)).isEmpty();
    }

    @Test
    void ssoShouldRedirectWithCodeAndState() throws Exception {
        // Arrange

        // Act
        MvcResult result = mockMvc.perform(get("/api/auth/sso"))
                .andExpect(status().isFound())
                .andExpect(header().exists("Location"))
                .andReturn();

        // Assert HTTP
        String locationHeader = result.getResponse().getHeader("Location");
        assertThat(locationHeader).isNotNull();
        URI location = URI.create(locationHeader);
        String query = location.getQuery();
        assertThat(query).isNotNull();
        assertThat(query).contains("code=");
        assertThat(query).contains("state=");

        // Assert Database
        assertThat(countAllRefreshTokens()).isEqualTo(0L);
    }


    @Test
    void protectedEndpointShouldReturn401WithoutJwt() throws Exception {
        // Arrange
        long tokensBefore = countAllRefreshTokens();

        // Act
        MvcResult result = mockMvc.perform(get("/api/protected"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        // Assert HTTP
        JsonNode body = readBody(result);
        assertThat(result.getResponse().getStatus()).isEqualTo(401);
        assertThat(body.path("success").asBoolean()).isFalse();
        assertThat(body.path("code").asText()).isNotBlank();
        assertThat(body.path("message").asText()).isNotBlank();

        // Assert Database
        assertThat(countAllRefreshTokens()).isEqualTo(tokensBefore);
    }

    private MvcResult login(String email, String password) throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("password", password);

        return mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    private MvcResult refresh(String refreshToken) throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("refreshToken", refreshToken);

        return mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    private JsonNode readBody(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        if (content.trim().isEmpty()) {
            return objectMapper.createObjectNode();
        }
        return objectMapper.readTree(content);
    }

    private Optional<RefreshTokenEntity> findRefreshToken(UUID tokenId) {
        entityManager.flush();
        entityManager.clear();
        return refreshTokenRepository.findById(tokenId);
    }

    private long countRefreshTokensByUser(UUID userId) {
        entityManager.flush();
        entityManager.clear();
        return refreshTokenRepository.findAll()
                .stream()
                .filter(token -> token.getUser().getId().equals(userId))
                .count();
    }

    private long countAllRefreshTokens() {
        entityManager.flush();
        entityManager.clear();
        return refreshTokenRepository.count();
    }

    private UUID findUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AssertionError("User not found"))
                .getId();
    }


}
