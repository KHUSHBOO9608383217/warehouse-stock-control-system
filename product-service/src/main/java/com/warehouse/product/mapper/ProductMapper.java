package com.warehouse.product.mapper;

import com.warehouse.product.dto.request.ProductRequest;
import com.warehouse.product.dto.response.ProductResponse;
import com.warehouse.product.entity.Product;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Product entity and DTOs.
 * Keeps conversion logic separate from business logic.
 */
@Component
public class ProductMapper {

    /**
     * Convert a ProductRequest DTO to a Product entity.
     */
    public Product toEntity(ProductRequest request) {
        return Product.builder()
                .productCode(request.getProductCode())
                .name(request.getName())
                .category(request.getCategory())
                .description(request.getDescription())
                .unit(request.getUnit())
                .minimumStockLevel(request.getMinimumStockLevel())
                .active(true)
                .build();
    }

    /**
     * Convert a Product entity to a ProductResponse DTO.
     */
    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .category(product.getCategory())
                .description(product.getDescription())
                .unit(product.getUnit())
                .minimumStockLevel(product.getMinimumStockLevel())
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    /**
     * Update an existing Product entity from a ProductRequest DTO.
     * Does not change id, productCode, active, or timestamps.
     */
    public void updateEntity(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setDescription(request.getDescription());
        product.setUnit(request.getUnit());
        product.setMinimumStockLevel(request.getMinimumStockLevel());
    }
}
