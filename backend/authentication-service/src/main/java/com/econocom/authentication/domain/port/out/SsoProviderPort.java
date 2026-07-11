package com.econocom.authentication.domain.port.out;

import com.econocom.authentication.domain.model.SsoCallbackResult;

public interface SsoProviderPort {

    String buildAuthorizationUrl(String state);

    SsoCallbackResult validateCallback(String code);

}

