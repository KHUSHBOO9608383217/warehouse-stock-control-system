package com.warehouse.stockmovement.mapper;

import com.warehouse.stockmovement.dto.request.StockMovementRequest;
import com.warehouse.stockmovement.dto.response.StockMovementResponse;
import com.warehouse.stockmovement.entity.StockMovement;
import org.springframework.stereotype.Component;

@Component
public class StockMovementMapper {

    public StockMovement toEntity(StockMovementRequest request) {
        return StockMovement.builder()
                .productId(request.getProductId())
                .warehouseId(request.getWarehouseId())
                .destinationWarehouseId(request.getDestinationWarehouseId())
                .quantity(request.getQuantity())
                .movementType(request.getMovementType())
                .referenceNumber(request.getReferenceNumber())
                .remarks(request.getRemarks())
                .build();
    }

    public StockMovementResponse toResponse(StockMovement movement) {
        return StockMovementResponse.builder()
                .id(movement.getId())
                .productId(movement.getProductId())
                .warehouseId(movement.getWarehouseId())
                .destinationWarehouseId(movement.getDestinationWarehouseId())
                .quantity(movement.getQuantity())
                .movementType(movement.getMovementType())
                .referenceNumber(movement.getReferenceNumber())
                .remarks(movement.getRemarks())
                .createdAt(movement.getCreatedAt())
                .build();
    }
}
