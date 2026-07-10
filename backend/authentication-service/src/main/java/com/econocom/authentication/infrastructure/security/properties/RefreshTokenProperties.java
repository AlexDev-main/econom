package com.econocom.authentication.infrastructure.security.properties;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
public class RefreshTokenProperties {

    @Min(60000)
    private long expiration;

}
