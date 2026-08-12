package com.warehouse.stockmovement.dto.response;

import com.warehouse.stockmovement.entity.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponse {

    private Long id;
    private Long productId;
    private Long warehouseId;
    private Long destinationWarehouseId;
    private Integer quantity;
    private MovementType movementType;
    private String referenceNumber;
    private String remarks;
    private LocalDateTime createdAt;
}
