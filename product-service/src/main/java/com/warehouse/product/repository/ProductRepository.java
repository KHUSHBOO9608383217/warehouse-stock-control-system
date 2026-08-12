package com.warehouse.product.repository;

import com.warehouse.product.entity.Category;
import com.warehouse.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Product entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find a product by its unique product code.
     */
    Optional<Product> findByProductCode(String productCode);

    /**
     * Check if a product with the given code exists.
     */
    boolean existsByProductCode(String productCode);

    /**
     * Find all products in a specific category.
     */
    List<Product> findByCategory(Category category);

    /**
     * Search products by name (case-insensitive partial match).
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Find all active products.
     */
    List<Product> findByActiveTrue();

    /**
     * Find active products by category.
     */
    List<Product> findByCategoryAndActiveTrue(Category category);
}
