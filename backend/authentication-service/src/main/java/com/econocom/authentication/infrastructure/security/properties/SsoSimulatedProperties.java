package com.econocom.authentication.infrastructure.security.properties;

import com.econocom.authentication.domain.model.SsoProvider;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SsoSimulatedProperties {

    @NotBlank
    private String redirectUri = "http://localhost:8080/api/auth/sso/callback";

    private SsoProvider provider = SsoProvider.SIMULATED;

    @NotBlank
    private String codePrefix = "sim-code-";

}

