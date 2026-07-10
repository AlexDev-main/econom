package com.econocom.authentication.domain.exception;

import com.econocom.authentication.shared.error.ErrorCode;

public class UserDisabledException extends BusinessException {

    public UserDisabledException() {
        super(ErrorCode.USER_DISABLED);
    }

}

