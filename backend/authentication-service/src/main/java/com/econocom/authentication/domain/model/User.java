package com.econocom.authentication.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;

    private String email;

    private String password;

    private Role role;

    private boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
