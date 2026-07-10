package com.econocom.authentication.domain.exception;

import com.econocom.authentication.shared.error.ErrorCode;

public class RefreshTokenRevokedException extends BusinessException {

    public RefreshTokenRevokedException() {
        super(ErrorCode.REFRESH_TOKEN_REVOKED);
    }

}

