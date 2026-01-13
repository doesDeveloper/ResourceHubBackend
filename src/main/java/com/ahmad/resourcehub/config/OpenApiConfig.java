package com.ahmad.resourcehub.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    final String securitySchemeName = "bearerAuth";

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("ResourceHub API")
                        .version("1.0.0")
                        .description("""
                                ##  Platform Overview
                                The **Resource Hub** is a centralized ecosystem for managing, sharing, and curating digital assets. \
                                It combines secure file storage with social engagement features.
                                
                                ###  Core Modules
                                * **Authentication:** JWT-based security. Login to receive a `Bearer Token`.
                                * **Hierarchy:**\s
                                    * **Super Folders:** Root-level containers.
                                    * **Nested Folders:** Infinite depth sub-directories.
                                * **File Management:** Upload resources with `author`, `description`, and metadata.
                                * **Engagement:**\s
                                    * **Ratings:** 1-5 star quality assessment.
                                    * **Bookmarks:** Save items for quick access.
                                
                                ###  Usage Instructions
                                1. Use the **Authentication** endpoints to register/login.
                                2. Copy the `token` from the response.
                                3. Click the **Authorize** button (padlock icon) at the top right.
                                4. Paste just the token""")
                        .termsOfService("http://swagger.io/terms")
                        .contact(new Contact().name("Ahmad Umar Usmani").email("ahmad@gmail.com").url("https://github.com/doesDeveloper"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)).components(
                        new Components().addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .scheme("bearer")
                                        .type(SecurityScheme.Type.HTTP)
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token in the format: Bearer 'token'")));
    }
}
