package com.econocom.authentication.application.usecase.auth;

import com.econocom.authentication.application.dto.auth.request.RefreshTokenRequest;
import com.econocom.authentication.application.service.auth.RefreshTokenValidationService;
import com.econocom.authentication.domain.model.RefreshToken;
import com.econocom.authentication.domain.port.out.RefreshTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {

    private final RefreshTokenValidationService refreshTokenValidationService;

    private final RefreshTokenRepositoryPort refreshTokenRepository;

    @Transactional
    public void execute(RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenValidationService.validate(request.getRefreshToken());

        refreshTokenRepository.revokeById(refreshToken.getId());

    }

}

