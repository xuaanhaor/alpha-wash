package com.alphawash.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class OpenAPIConfiguration {
    @Bean
    public OpenAPI apiInfo() {
        Info info = new Info();
        info.setTitle("Alpha Wash API");
        info.setDescription("API documentation for Alpha Wash application");
        info.setVersion("0.0.3-SNAPSHOT");
        info.setSummary("Alpha Wash API Documentation");
        return new OpenAPI().info(info);
    }
}
