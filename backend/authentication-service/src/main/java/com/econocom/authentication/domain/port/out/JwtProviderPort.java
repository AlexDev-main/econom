package com.econocom.authentication.domain.port.out;

import com.econocom.authentication.domain.model.JwtPayload;
import com.econocom.authentication.domain.model.User;

public interface JwtProviderPort {

    String generateAccessToken(User user);

    JwtPayload parseToken(String token);
}
