package com.econocom.authentication.infrastructure.security.properties;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class JwtProperties {

    /**
     * Clave utilizada para firmar el Access Token.
     */
    @NotBlank
    private String secret;

    /**
     * Tiempo de expiración del Access Token en milisegundos.
     */
    @Min(60000)
    private long expiration;

}
