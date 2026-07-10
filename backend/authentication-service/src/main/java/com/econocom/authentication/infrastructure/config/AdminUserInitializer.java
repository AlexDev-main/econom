package com.econocom.authentication.infrastructure.config;

import com.econocom.authentication.domain.model.Role;
import com.econocom.authentication.domain.model.User;
import com.econocom.authentication.domain.port.out.PasswordEncoderPort;
import com.econocom.authentication.domain.port.out.UserRepositoryPort;
import com.econocom.authentication.infrastructure.config.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final AppProperties appProperties;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    @Override
    public void run(String... args) {

        String email = appProperties.getAdmin().getEmail();

        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }

        User admin = User.builder()
                .email(email)
                .password(passwordEncoder.encode(appProperties.getAdmin().getPassword()))
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        userRepository.save(admin);

    }

}