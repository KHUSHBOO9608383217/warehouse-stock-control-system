package com.warehouse.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for Product Service.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI productServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Service API")
                        .description("REST API for managing laptop component products - "
                                + "processors, RAM, SSDs, motherboards, displays, batteries, etc.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Warehouse Management Team")));
    }
}
