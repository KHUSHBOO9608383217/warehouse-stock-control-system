package com.warehouse.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warehouse.product.dto.request.ProductRequest;
import com.warehouse.product.dto.response.ProductResponse;
import com.warehouse.product.entity.Category;
import com.warehouse.product.exception.ResourceNotFoundException;
import com.warehouse.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController Unit Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/products - Should create product")
    void createProduct() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .productCode("PROC-001")
                .name("Intel Core i5")
                .category(Category.PROCESSOR)
                .unit("PCS")
                .minimumStockLevel(50)
                .build();

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .productCode("PROC-001")
                .name("Intel Core i5")
                .category(Category.PROCESSOR)
                .active(true)
                .build();

        when(productService.createProduct(any())).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productCode").value("PROC-001"));
    }

    @Test
    @DisplayName("GET /api/products - Should get all products")
    void getAllProducts() throws Exception {
        ProductResponse p1 = ProductResponse.builder().id(1L).name("Product 1").build();
        ProductResponse p2 = ProductResponse.builder().id(2L).name("Product 2").build();

        when(productService.getAllProducts()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/products/{id} - Should return 404 when not found")
    void getProductById_NotFound() throws Exception {
        when(productService.getProductById(99L))
                .thenThrow(new ResourceNotFoundException("Product", "id", 99L));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/products - Should return 400 for invalid request")
    void createProduct_ValidationError() throws Exception {
        ProductRequest invalidRequest = ProductRequest.builder()
                .productCode("") // blank - should fail validation
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
