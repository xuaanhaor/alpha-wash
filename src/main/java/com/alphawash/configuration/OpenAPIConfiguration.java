package com.alphawash.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class OpenAPIConfiguration {
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info());
    }
}
