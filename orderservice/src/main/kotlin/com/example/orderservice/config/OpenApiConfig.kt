package com.example.orderservice.config

import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

//for swgger page
@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {

        // Define the security scheme (Bearer Token)
        val securitySchemeName = "bearer-key"

        return OpenAPI()
            .components(
                Components()
                    .addSecuritySchemes(
                        securitySchemeName, SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
            .addSecurityItem(SecurityRequirement().addList(securitySchemeName))
            .info(
                Info()
                    .title("order service Auth API")
                    .version("1.0.0")
                    .description("Nexus flow auth API")
            )

    }
}