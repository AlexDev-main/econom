package com.econocom.authentication.domain.exception;

import com.econocom.authentication.shared.error.ErrorCode;

public class InvalidAuthorizationCodeException extends BusinessException {

    public InvalidAuthorizationCodeException() {
        super(ErrorCode.INVALID_SSO_CALLBACK);
    }

}

