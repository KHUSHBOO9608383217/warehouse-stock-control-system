package com.warehouse.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to hold product information fetched from Product Service.
 * This is a lightweight representation - we only need the fields
 * relevant to inventory operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String productCode;
    private String name;
    private String category;
    private Integer minimumStockLevel;
    private Boolean active;
}
