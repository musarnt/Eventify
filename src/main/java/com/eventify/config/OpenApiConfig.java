package com.eventify.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eventifyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Eventify API")
                        .description("API para gestión de eventos y lugares")
                        .version("1.0.0"));
    }
}