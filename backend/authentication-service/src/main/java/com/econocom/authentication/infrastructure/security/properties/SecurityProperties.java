package com.econocom.authentication.infrastructure.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private JwtProperties jwt = new JwtProperties();

    private RefreshTokenProperties refreshToken = new RefreshTokenProperties();

}
