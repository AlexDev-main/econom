package com.econocom.authentication.infrastructure.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.convert.DurationUnit;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class SsoProperties {

    @NotNull
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration stateExpiration = Duration.ofMinutes(5);

    @NotNull
    @DurationUnit(ChronoUnit.MILLIS)
    private Duration authorizationCodeExpiration = Duration.ofMinutes(5);

    @Valid
    private SsoSimulatedProperties simulated = new SsoSimulatedProperties();

}

