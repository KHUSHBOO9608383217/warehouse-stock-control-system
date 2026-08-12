package com.warehouse.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Inventory entity representing the stock of a product in a warehouse.
 * 
 * Uses productId and warehouseId (Long references) rather than JPA relationships
 * because Product and Warehouse are owned by separate microservices with their
 * own databases. Direct @ManyToOne relationships would violate service boundaries.
 *
 * Business rule: availableQuantity = quantity - reservedQuantity
 */
@Entity
@Table(name = "inventory", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_warehouse", columnNames = {"productId", "warehouseId"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long warehouseId;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer reservedQuantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer availableQuantity = 0;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Recalculates the available quantity based on the business rule.
     * Should be called whenever quantity or reservedQuantity changes.
     */
    public void recalculateAvailableQuantity() {
        this.availableQuantity = this.quantity - this.reservedQuantity;
    }
}
