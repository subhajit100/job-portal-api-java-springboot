package com.subhajit.job_portal_api.config;

import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;
import static io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER;

// this will add support for JWT Bearer auth in Swagger UI as well
@Configuration
@SecurityScheme(
        name = "bearerAuth", // Name for the security scheme
        type = HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = HEADER
)
public class SwaggerConfiguration {
}
