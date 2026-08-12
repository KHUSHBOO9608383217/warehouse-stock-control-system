package com.warehouse.product.service;

import com.warehouse.product.dto.request.ProductRequest;
import com.warehouse.product.dto.response.ProductResponse;
import com.warehouse.product.entity.Category;

import java.util.List;

/**
 * Service interface for Product business operations.
 * Defines the contract that the implementation must fulfill.
 */
public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(Long id);

    List<ProductResponse> getAllProducts();

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    List<ProductResponse> getProductsByCategory(Category category);

    List<ProductResponse> searchProductsByName(String name);
}
