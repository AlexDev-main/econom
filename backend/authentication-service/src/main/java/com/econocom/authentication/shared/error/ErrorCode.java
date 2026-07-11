package com.econocom.authentication.shared.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND(
            404,
            "AUTH-001",
            "User not found."
    ),

    INVALID_CREDENTIALS(
            401,
            "AUTH-002",
            "Invalid credentials."
    ),

    USER_DISABLED(
            403,
            "AUTH-003",
            "User account is disabled."
    ),

    INVALID_REFRESH_TOKEN(
            401,
            "AUTH-004",
            "Invalid refresh token."
    ),

    REFRESH_TOKEN_REVOKED(
            401,
            "AUTH-005",
            "Refresh token has been revoked."
    ),

    REFRESH_TOKEN_NOT_FOUND(
        404,
                "AUTH-006",
                "Refresh token not found."
    ),

    REFRESH_TOKEN_EXPIRED(
            401,
            "AUTH-007",
            "Refresh token has expired."
    );

    private final int httpStatus;

    private final String code;

    private final String defaultMessage;

    ErrorCode(int httpStatus,
              String code,
              String defaultMessage) {

        this.httpStatus = httpStatus;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

}
