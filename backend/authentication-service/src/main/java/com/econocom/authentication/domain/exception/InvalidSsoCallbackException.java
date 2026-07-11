package com.econocom.authentication.domain.exception;

import com.econocom.authentication.shared.error.ErrorCode;

public class InvalidSsoCallbackException extends BusinessException {

    public InvalidSsoCallbackException() {
        super(ErrorCode.INVALID_SSO_CALLBACK);
    }

}

