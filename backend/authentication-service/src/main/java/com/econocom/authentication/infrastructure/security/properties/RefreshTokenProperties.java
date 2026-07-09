package com.econocom.authentication.infrastructure.security.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenProperties {

    private long expiration;

}
