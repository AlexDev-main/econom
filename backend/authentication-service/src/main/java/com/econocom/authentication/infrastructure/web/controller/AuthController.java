package com.econocom.authentication.infrastructure.web.controller;

import com.econocom.authentication.application.dto.auth.request.LoginRequest;
import com.econocom.authentication.application.dto.auth.request.RefreshTokenRequest;
import com.econocom.authentication.application.dto.auth.response.TokenResponse;
import com.econocom.authentication.application.usecase.auth.LoginUseCase;
import com.econocom.authentication.application.usecase.auth.RefreshTokenUseCase;
import com.econocom.authentication.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;

    private final RefreshTokenUseCase refreshTokenUseCase;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        TokenResponse tokenResponse = loginUseCase.execute(request);

        ApiResponse<TokenResponse> response = ApiResponse.<TokenResponse>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Login successful.")
                .data(tokenResponse)
                .build();

        return ResponseEntity.ok(response);

    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        TokenResponse tokenResponse = refreshTokenUseCase.execute(request);

        ApiResponse<TokenResponse> response = ApiResponse.<TokenResponse>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Refresh successful.")
                .data(tokenResponse)
                .build();

        return ResponseEntity.ok(response);

    }

}
