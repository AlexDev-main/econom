package com.econocom.authentication.infrastructure.security.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtProperties {

    /**
     * Clave utilizada para firmar el Access Token.
     */
    private String secret;

    /**
     * Tiempo de expiración del Access Token en milisegundos.
     */
    private long expiration;

}
