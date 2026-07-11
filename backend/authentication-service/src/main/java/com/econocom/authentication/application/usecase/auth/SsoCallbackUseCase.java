package com.econocom.authentication.application.usecase.auth;

import com.econocom.authentication.application.dto.auth.response.TokenResponse;
import com.econocom.authentication.application.service.auth.AuthenticationTokenService;
import com.econocom.authentication.application.service.auth.AuthorizationCodeService;
import com.econocom.authentication.application.service.auth.SsoStateService;
import com.econocom.authentication.domain.exception.UserDisabledException;
import com.econocom.authentication.domain.exception.UserNotFoundException;
import com.econocom.authentication.domain.model.SsoCallbackResult;
import com.econocom.authentication.domain.model.User;
import com.econocom.authentication.domain.port.out.RefreshTokenRepositoryPort;
import com.econocom.authentication.domain.port.out.SsoProviderPort;
import com.econocom.authentication.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SsoCallbackUseCase {

    private final SsoStateService ssoStateService;

    private final SsoProviderPort ssoProvider;

    private final AuthorizationCodeService authorizationCodeService;

    private final UserRepositoryPort userRepository;

    private final RefreshTokenRepositoryPort refreshTokenRepository;

    private final AuthenticationTokenService authenticationTokenService;

    @Transactional
    public TokenResponse execute(String code, String state) {

        ssoStateService.consume(state);

        authorizationCodeService.consume(code);

        SsoCallbackResult callbackResult = ssoProvider.validateCallback(code);

        User user = loadUser(callbackResult.getEmail());

        validateUser(user);

        refreshTokenRepository.revokeAllActiveByUser(user.getId());

        return authenticationTokenService.issue(user);
    }

    private User loadUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    private void validateUser(User user) {

        if (!user.isEnabled()) {
            throw new UserDisabledException();
        }
    }

}

