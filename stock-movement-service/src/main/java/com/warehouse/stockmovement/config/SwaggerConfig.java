package com.warehouse.stockmovement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI stockMovementServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stock Movement Service API")
                        .description("REST API for recording stock movements - IN, OUT, and TRANSFER operations")
                        .version("1.0.0")
                        .contact(new Contact().name("Warehouse Management Team")));
    }
}
