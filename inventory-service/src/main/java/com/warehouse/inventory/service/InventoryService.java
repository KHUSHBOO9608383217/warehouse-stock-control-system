package com.warehouse.inventory.service;

import com.warehouse.inventory.dto.request.InventoryRequest;
import com.warehouse.inventory.dto.response.InventoryResponse;

import java.util.List;

public interface InventoryService {

    InventoryResponse createInventory(InventoryRequest request);

    InventoryResponse getInventoryById(Long id);

    List<InventoryResponse> getAllInventory();

    InventoryResponse updateInventory(Long id, InventoryRequest request);

    void deleteInventory(Long id);

    List<InventoryResponse> getInventoryByProductId(Long productId);

    List<InventoryResponse> getInventoryByWarehouseId(Long warehouseId);

    List<InventoryResponse> getLowStockInventory();

    /**
     * Called by Stock Movement Service to add stock (STOCK IN).
     */
    void addStock(Long productId, Long warehouseId, int quantity);

    /**
     * Called by Stock Movement Service to remove stock (STOCK OUT).
     */
    void removeStock(Long productId, Long warehouseId, int quantity);
}
