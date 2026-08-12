package com.warehouse.warehouseservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI warehouseServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Warehouse Service API")
                        .description("REST API for managing warehouse/storage locations")
                        .version("1.0.0")
                        .contact(new Contact().name("Warehouse Management Team")));
    }
}
