package com.warehouse.stockmovement.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * REST client for communicating with Inventory Service.
 * Used to update inventory quantities when stock movements occur.
 */
@Component
public class InventoryServiceClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceClient.class);

    private final RestTemplate restTemplate;
    private final String inventoryServiceUrl;

    public InventoryServiceClient(RestTemplate restTemplate,
                                  @Value("${service.inventory-service.url}") String inventoryServiceUrl) {
        this.restTemplate = restTemplate;
        this.inventoryServiceUrl = inventoryServiceUrl;
    }

    /**
     * Calls Inventory Service to add stock.
     */
    public void addStock(Long productId, Long warehouseId, int quantity) {
        log.info("Calling Inventory Service to add {} units for productId: {} warehouseId: {}",
                quantity, productId, warehouseId);

        String url = UriComponentsBuilder.fromHttpUrl(inventoryServiceUrl + "/api/inventory/add-stock")
                .queryParam("productId", productId)
                .queryParam("warehouseId", warehouseId)
                .queryParam("quantity", quantity)
                .toUriString();

        restTemplate.put(url, null);
        log.info("Inventory Service add-stock call completed");
    }

    /**
     * Calls Inventory Service to remove stock.
     */
    public void removeStock(Long productId, Long warehouseId, int quantity) {
        log.info("Calling Inventory Service to remove {} units for productId: {} warehouseId: {}",
                quantity, productId, warehouseId);

        String url = UriComponentsBuilder.fromHttpUrl(inventoryServiceUrl + "/api/inventory/remove-stock")
                .queryParam("productId", productId)
                .queryParam("warehouseId", warehouseId)
                .queryParam("quantity", quantity)
                .toUriString();

        restTemplate.put(url, null);
        log.info("Inventory Service remove-stock call completed");
    }

    /**
     * Gets the current stock quantity for a product in a warehouse.
     * Returns 0 if unable to fetch.
     */
    @SuppressWarnings("unchecked")
    public int getCurrentStock(Long productId, Long warehouseId) {
        try {
            String url = inventoryServiceUrl + "/api/inventory/product/" + productId;
            java.util.Map<String, Object> response = restTemplate.getForObject(url, java.util.Map.class);
            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                java.util.List<java.util.Map<String, Object>> data =
                        (java.util.List<java.util.Map<String, Object>>) response.get("data");
                if (data != null) {
                    return data.stream()
                            .filter(inv -> warehouseId.equals(Long.valueOf(inv.get("warehouseId").toString())))
                            .findFirst()
                            .map(inv -> Integer.valueOf(inv.get("quantity").toString()))
                            .orElse(0);
                }
            }
        } catch (Exception e) {
            log.warn("Could not fetch current stock: {}", e.getMessage());
        }
        return 0;
    }
}
