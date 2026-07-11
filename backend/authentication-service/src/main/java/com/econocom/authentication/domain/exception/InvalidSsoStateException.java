package com.econocom.authentication.domain.exception;

import com.econocom.authentication.shared.error.ErrorCode;

public class InvalidSsoStateException extends BusinessException {

    public InvalidSsoStateException() {
        super(ErrorCode.INVALID_SSO_STATE);
    }

}

