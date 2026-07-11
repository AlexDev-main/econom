package com.econocom.authentication.application.usecase.auth;

import com.econocom.authentication.application.service.auth.SsoStateService;
import com.econocom.authentication.domain.port.out.SsoProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class SsoRedirectUseCase {

    private final SsoStateService ssoStateService;

    private final SsoProviderPort ssoProvider;

    public URI execute() {

        String state = ssoStateService.generateAndStore();

        return URI.create(ssoProvider.buildAuthorizationUrl(state));
    }

}

