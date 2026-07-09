package com.econocom.authentication.domain.port.out;

public interface PasswordEncoderPort {

    String encode(String rawValue);

    boolean matches(String rawValue, String encodedValue);

}
