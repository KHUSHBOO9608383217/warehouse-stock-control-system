package com.warehouse.stockmovement.client;

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
 * Used to fetch product information for low-stock checking.
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
     * Gets the minimum stock level for a product.
     * Returns 0 if the product service is unavailable.
     */
    public int getMinimumStockLevel(Long productId) {
        try {
            String url = productServiceUrl + "/api/products/" + productId;
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<Map<String, Object>>() {});

            Map<String, Object> body = response.getBody();
            if (body != null && Boolean.TRUE.equals(body.get("success"))) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                if (data != null && data.get("minimumStockLevel") != null) {
                    return Integer.parseInt(data.get("minimumStockLevel").toString());
                }
            }
        } catch (Exception e) {
            log.warn("Could not fetch minimum stock level for productId {}: {}", productId, e.getMessage());
        }
        return 0;
    }

    /**
     * Checks if a product is active.
     */
    public boolean isProductActive(Long productId) {
        try {
            String url = productServiceUrl + "/api/products/" + productId;
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<Map<String, Object>>() {});

            Map<String, Object> body = response.getBody();
            if (body != null && Boolean.TRUE.equals(body.get("success"))) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                if (data != null) {
                    return Boolean.TRUE.equals(data.get("active"));
                }
            }
        } catch (Exception e) {
            log.warn("Could not check product active status for productId {}: {}", productId, e.getMessage());
        }
        return true; // default to true if service unavailable
    }
}
