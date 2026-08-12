package com.warehouse.product.service.impl;

import com.warehouse.product.dto.request.ProductRequest;
import com.warehouse.product.dto.response.ProductResponse;
import com.warehouse.product.entity.Category;
import com.warehouse.product.entity.Product;
import com.warehouse.product.exception.DuplicateResourceException;
import com.warehouse.product.exception.ResourceNotFoundException;
import com.warehouse.product.mapper.ProductMapper;
import com.warehouse.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl Unit Tests")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest productRequest;
    private Product product;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        productRequest = ProductRequest.builder()
                .productCode("PROC-001")
                .name("Intel Core i5 Processor")
                .category(Category.PROCESSOR)
                .description("12th Gen Intel Core i5")
                .unit("PCS")
                .minimumStockLevel(50)
                .build();

        product = Product.builder()
                .id(1L)
                .productCode("PROC-001")
                .name("Intel Core i5 Processor")
                .category(Category.PROCESSOR)
                .description("12th Gen Intel Core i5")
                .unit("PCS")
                .minimumStockLevel(50)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productResponse = ProductResponse.builder()
                .id(1L)
                .productCode("PROC-001")
                .name("Intel Core i5 Processor")
                .category(Category.PROCESSOR)
                .description("12th Gen Intel Core i5")
                .unit("PCS")
                .minimumStockLevel(50)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should create product successfully")
    void createProduct_Success() {
        when(productRepository.existsByProductCode("PROC-001")).thenReturn(false);
        when(productMapper.toEntity(productRequest)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.createProduct(productRequest);

        assertNotNull(result);
        assertEquals("PROC-001", result.getProductCode());
        assertEquals("Intel Core i5 Processor", result.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when product code exists")
    void createProduct_DuplicateCode() {
        when(productRepository.existsByProductCode("PROC-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> productService.createProduct(productRequest));

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get product by ID successfully")
    void getProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product not found")
    void getProductById_NotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(99L));
    }

    @Test
    @DisplayName("Should get all products")
    void getAllProducts() {
        Product product2 = Product.builder().id(2L).productCode("RAM-001").name("8GB RAM").build();
        ProductResponse response2 = ProductResponse.builder().id(2L).productCode("RAM-001").name("8GB RAM").build();

        when(productRepository.findAll()).thenReturn(Arrays.asList(product, product2));
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        when(productMapper.toResponse(product2)).thenReturn(response2);

        List<ProductResponse> results = productService.getAllProducts();

        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("Should update product successfully")
    void updateProduct_Success() {
        ProductRequest updateRequest = ProductRequest.builder()
                .productCode("PROC-001")
                .name("Intel Core i5 Processor Updated")
                .category(Category.PROCESSOR)
                .unit("PCS")
                .minimumStockLevel(60)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findByProductCode("PROC-001")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        ProductResponse result = productService.updateProduct(1L, updateRequest);

        assertNotNull(result);
        verify(productMapper).updateEntity(product, updateRequest);
    }

    @Test
    @DisplayName("Should deactivate product (soft delete)")
    void deleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        assertFalse(product.getActive());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Should get products by category")
    void getProductsByCategory() {
        when(productRepository.findByCategoryAndActiveTrue(Category.PROCESSOR))
                .thenReturn(Arrays.asList(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        List<ProductResponse> results = productService.getProductsByCategory(Category.PROCESSOR);

        assertEquals(1, results.size());
        assertEquals(Category.PROCESSOR, results.get(0).getCategory());
    }

    @Test
    @DisplayName("Should search products by name")
    void searchProductsByName() {
        when(productRepository.findByNameContainingIgnoreCase("Intel"))
                .thenReturn(Arrays.asList(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        List<ProductResponse> results = productService.searchProductsByName("Intel");

        assertEquals(1, results.size());
        assertTrue(results.get(0).getName().contains("Intel"));
    }
}
