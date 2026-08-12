package com.warehouse.inventory.mapper;

import com.warehouse.inventory.dto.request.InventoryRequest;
import com.warehouse.inventory.dto.response.InventoryResponse;
import com.warehouse.inventory.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public Inventory toEntity(InventoryRequest request) {
        Inventory inventory = Inventory.builder()
                .productId(request.getProductId())
                .warehouseId(request.getWarehouseId())
                .quantity(request.getQuantity())
                .reservedQuantity(request.getReservedQuantity() != null ? request.getReservedQuantity() : 0)
                .build();
        inventory.recalculateAvailableQuantity();
        return inventory;
    }

    public InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .warehouseId(inventory.getWarehouseId())
                .quantity(inventory.getQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .availableQuantity(inventory.getAvailableQuantity())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}
