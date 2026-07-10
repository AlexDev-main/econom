package com.econocom.authentication.domain.exception;

import com.econocom.authentication.shared.error.ErrorCode;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String email) {
        super(ErrorCode.USER_NOT_FOUND, email);
    }

}

