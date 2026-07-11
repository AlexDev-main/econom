package com.econocom.authentication.infrastructure.external.sso;

import com.econocom.authentication.domain.exception.InvalidSsoCallbackException;
import com.econocom.authentication.domain.model.SsoCallbackResult;
import com.econocom.authentication.domain.port.out.SsoProviderPort;
import com.econocom.authentication.application.service.auth.AuthorizationCodeService;
import com.econocom.authentication.infrastructure.config.properties.AppProperties;
import com.econocom.authentication.infrastructure.security.properties.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class SimulatedSsoProviderAdapter implements SsoProviderPort {

    private final SecurityProperties securityProperties;

    private final AppProperties appProperties;

    private final AuthorizationCodeService authorizationCodeService;

    @Override
    public String buildAuthorizationUrl(String state) {

        String code = authorizationCodeService.generateAndStore();

        String redirectUri = securityProperties.getSso().getSimulated().getRedirectUri();

        return UriComponentsBuilder.fromHttpUrl(redirectUri)
                .queryParam("code", code)
                .queryParam("state", state)
                .build(true)
                .toUriString();
    }

    @Override
    public SsoCallbackResult validateCallback(String code) {

        if (code == null || code.trim().isEmpty()) {
            throw new InvalidSsoCallbackException();
        }

        String adminEmail = appProperties.getAdmin().getEmail();

        if (adminEmail == null || adminEmail.trim().isEmpty()) {
            throw new InvalidSsoCallbackException();
        }

        String codePrefix = securityProperties.getSso().getSimulated().getCodePrefix();

        return SsoCallbackResult.builder()
                .email(adminEmail)
                .provider(securityProperties.getSso().getSimulated().getProvider())
                .providerUserId(stripPrefix(code, codePrefix))
                .build();
    }

    private String stripPrefix(String code, String prefix) {

        if (code.startsWith(prefix)) {
            return code.substring(prefix.length());
        }

        return code;
    }

}

