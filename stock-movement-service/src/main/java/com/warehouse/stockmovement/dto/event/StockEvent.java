package com.warehouse.stockmovement.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Base event class for stock events published to Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockEvent {

    private String eventType;
    private Long productId;
    private Long warehouseId;
    private Long destinationWarehouseId;
    private Integer quantity;
    private Integer currentStock;
    private Integer minimumStockLevel;
    private String referenceNumber;
    private String message;
    private LocalDateTime timestamp;
}
