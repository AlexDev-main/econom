package com.econocom.authentication.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtPayload {

    private String email;

    private Role role;

    private Instant issuedAt;

    private Instant expiresAt;

}
