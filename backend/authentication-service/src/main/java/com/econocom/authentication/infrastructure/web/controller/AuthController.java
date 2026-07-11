package com.econocom.authentication.infrastructure.web.controller;

import com.econocom.authentication.application.dto.auth.request.LoginRequest;
import com.econocom.authentication.application.dto.auth.request.RefreshTokenRequest;
import com.econocom.authentication.application.dto.auth.response.TokenResponse;
import com.econocom.authentication.application.usecase.auth.LoginUseCase;
import com.econocom.authentication.application.usecase.auth.LogoutUseCase;
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

    private final LogoutUseCase logoutUseCase;

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

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        logoutUseCase.execute(request);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Logout successful.")
                .build();

        return ResponseEntity.ok(response);

    }

}
