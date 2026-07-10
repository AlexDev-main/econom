package com.econocom.authentication.infrastructure.security.properties;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class JwtProperties {

    /**
     * Clave utilizada para firmar el Access Token.
     */
    @NotBlank
    @Size(min = 32, message = "security.jwt.secret debe tener al menos 32 caracteres (256 bits para HS256)")
    private String secret;

    /**
     * Tiempo de expiración del Access Token en milisegundos.
     */
    @Min(60000)
    private long expiration;

}
