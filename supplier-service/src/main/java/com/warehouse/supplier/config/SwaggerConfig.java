package com.warehouse.supplier.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI supplierServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Supplier Service API")
                        .description("REST API for managing laptop component suppliers")
                        .version("1.0.0")
                        .contact(new Contact().name("Warehouse Management Team")));
    }
}
