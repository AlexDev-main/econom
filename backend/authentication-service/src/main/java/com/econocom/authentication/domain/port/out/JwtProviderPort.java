package com.econocom.authentication.domain.port.out;

import com.econocom.authentication.domain.model.User;

public interface JwtProviderPort {

    String generateAccessToken(User user);

    String generateRefreshToken();

    boolean validateToken(String token);

    String extractEmail(String token);

}
