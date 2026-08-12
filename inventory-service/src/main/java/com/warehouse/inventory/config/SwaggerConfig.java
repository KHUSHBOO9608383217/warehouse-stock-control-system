package com.warehouse.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI inventoryServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory Service API")
                        .description("REST API for managing inventory quantities of products in warehouses. "
                                + "Tracks stock levels, reserved quantities, and low-stock alerts.")
                        .version("1.0.0")
                        .contact(new Contact().name("Warehouse Management Team")));
    }
}
