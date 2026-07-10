package com.econocom.authentication.infrastructure.security.properties;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CorsProperties {

    @NotEmpty
    private List<String> allowedOrigins = new ArrayList<>();

    @NotEmpty
    private List<String> allowedMethods = new ArrayList<>();

    @NotEmpty
    private List<String> allowedHeaders = new ArrayList<>();

    private boolean allowCredentials;

}
