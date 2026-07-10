package com.econocom.authentication.infrastructure.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    @Valid
    private JwtProperties jwt = new JwtProperties();

    @Valid
    private RefreshTokenProperties refreshToken = new RefreshTokenProperties();

    @Valid
    private CorsProperties cors = new CorsProperties();

}
