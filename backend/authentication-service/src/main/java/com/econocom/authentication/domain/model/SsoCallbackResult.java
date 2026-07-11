package com.econocom.authentication.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SsoCallbackResult {

    private String email;

    private SsoProvider provider;

    private String providerUserId;

}

