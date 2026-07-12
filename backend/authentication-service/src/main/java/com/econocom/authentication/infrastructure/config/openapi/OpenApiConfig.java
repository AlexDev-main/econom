package com.econocom.authentication.infrastructure.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authenticationServiceOpenApi() {

        return new OpenAPI()
                .info(new Info()
                        .title("Authentication Service API")
                        .description("API de autenticacion con JWT, Refresh Token Rotation y SSO simulado.")
                        .version("v1")
                        .contact(new Contact()
                                .name("Equipo Backend")
                                .email("backend@econocom.local"))
                        .license(new License()
                                .name("Uso interno")
                                .url("https://example.local/internal-license")))
                .components(new Components().addSecuritySchemes(
                        "bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ));
    }

}

