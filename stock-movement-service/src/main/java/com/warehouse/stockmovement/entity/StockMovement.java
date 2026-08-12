package com.warehouse.stockmovement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * StockMovement entity recording a stock transaction.
 * Uses productId and warehouseId as references to entities in other services.
 * For TRANSFER type, destinationWarehouseId holds the target warehouse.
 */
@Entity
@Table(name = "stock_movements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long warehouseId;

    /**
     * Destination warehouse ID for TRANSFER movements.
     * Null for IN and OUT movements.
     */
    private Long destinationWarehouseId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MovementType movementType;

    @Column(length = 50)
    private String referenceNumber;

    @Column(length = 500)
    private String remarks;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
