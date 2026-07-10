package com.econocom.authentication.domain.exception;

import com.econocom.authentication.shared.error.ErrorCode;

public class InvalidRefreshTokenException extends BusinessException {

    public InvalidRefreshTokenException() {
        super(ErrorCode.INVALID_REFRESH_TOKEN);
    }

}

