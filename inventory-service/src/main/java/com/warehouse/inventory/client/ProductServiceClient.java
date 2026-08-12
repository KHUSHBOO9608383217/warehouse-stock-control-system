package com.warehouse.inventory.client;

import com.warehouse.inventory.dto.ProductDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * REST client for communicating with Product Service.
 * Uses Resilience4j Circuit Breaker to handle Product Service unavailability.
 * 
 * This demonstrates inter-service REST communication in a microservices architecture.
 * When Product Service is down, the circuit breaker opens and the fallback method
 * returns a default ProductDTO to prevent cascading failures.
 */
@Component
public class ProductServiceClient {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceClient.class);

    private final RestTemplate restTemplate;
    private final String productServiceUrl;

    public ProductServiceClient(RestTemplate restTemplate,
                                @Value("${service.product-service.url}") String productServiceUrl) {
        this.restTemplate = restTemplate;
        this.productServiceUrl = productServiceUrl;
    }

    /**
     * Fetches product information from Product Service.
     * Protected by a Circuit Breaker — if Product Service is unavailable,
     * the fallback method is called instead.
     */
    @CircuitBreaker(name = "productService", fallbackMethod = "getProductByIdFallback")
    public ProductDTO getProductById(Long productId) {
        log.info("Calling Product Service to fetch product with id: {}", productId);

        String url = productServiceUrl + "/api/products/" + productId;
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        Map<String, Object> body = response.getBody();
        if (body != null && Boolean.TRUE.equals(body.get("success"))) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            if (data != null) {
                return ProductDTO.builder()
                        .id(Long.valueOf(data.get("id").toString()))
                        .productCode((String) data.get("productCode"))
                        .name((String) data.get("name"))
                        .category((String) data.get("category"))
                        .minimumStockLevel(data.get("minimumStockLevel") != null
                                ? Integer.valueOf(data.get("minimumStockLevel").toString()) : 0)
                        .active((Boolean) data.get("active"))
                        .build();
            }
        }
        log.warn("Product Service returned unexpected response for product id: {}", productId);
        return null;
    }

    /**
     * Fallback method when Product Service is unavailable.
     * Returns a default ProductDTO so the caller can continue gracefully.
     */
    @SuppressWarnings("unused")
    private ProductDTO getProductByIdFallback(Long productId, Exception ex) {
        log.warn("Circuit breaker activated for Product Service. Product id: {}, Error: {}",
                productId, ex.getMessage());
        return ProductDTO.builder()
                .id(productId)
                .name("Unknown (Product Service unavailable)")
                .minimumStockLevel(0)
                .active(true)
                .build();
    }
}
