package com.warehouse.inventory.service.impl;

import com.warehouse.inventory.client.ProductServiceClient;
import com.warehouse.inventory.dto.request.InventoryRequest;
import com.warehouse.inventory.dto.response.InventoryResponse;
import com.warehouse.inventory.entity.Inventory;
import com.warehouse.inventory.exception.InsufficientStockException;
import com.warehouse.inventory.exception.ResourceNotFoundException;
import com.warehouse.inventory.mapper.InventoryMapper;
import com.warehouse.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryServiceImpl Unit Tests")
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory inventory;
    private InventoryResponse inventoryResponse;

    @BeforeEach
    void setUp() {
        inventory = Inventory.builder()
                .id(1L)
                .productId(1L)
                .warehouseId(1L)
                .quantity(100)
                .reservedQuantity(10)
                .availableQuantity(90)
                .build();

        inventoryResponse = InventoryResponse.builder()
                .id(1L)
                .productId(1L)
                .warehouseId(1L)
                .quantity(100)
                .reservedQuantity(10)
                .availableQuantity(90)
                .build();
    }

    @Test
    @DisplayName("Should create inventory successfully")
    void createInventory_Success() {
        InventoryRequest request = InventoryRequest.builder()
                .productId(1L).warehouseId(1L).quantity(100).reservedQuantity(0).build();

        when(inventoryRepository.existsByProductIdAndWarehouseId(1L, 1L)).thenReturn(false);
        when(inventoryMapper.toEntity(request)).thenReturn(inventory);
        when(inventoryRepository.save(any())).thenReturn(inventory);
        when(inventoryMapper.toResponse(inventory)).thenReturn(inventoryResponse);

        InventoryResponse result = inventoryService.createInventory(request);

        assertNotNull(result);
        assertEquals(1L, result.getProductId());
    }

    @Test
    @DisplayName("Should reject duplicate inventory for same product and warehouse")
    void createInventory_Duplicate() {
        InventoryRequest request = InventoryRequest.builder()
                .productId(1L).warehouseId(1L).quantity(100).reservedQuantity(0).build();

        when(inventoryRepository.existsByProductIdAndWarehouseId(1L, 1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> inventoryService.createInventory(request));
    }

    @Test
    @DisplayName("Should reject when reserved quantity exceeds total")
    void createInventory_ReservedExceedsTotal() {
        InventoryRequest request = InventoryRequest.builder()
                .productId(1L).warehouseId(1L).quantity(50).reservedQuantity(100).build();

        assertThrows(IllegalArgumentException.class,
                () -> inventoryService.createInventory(request));
    }

    @Test
    @DisplayName("Should add stock successfully")
    void addStock_Success() {
        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(inventory));

        inventoryService.addStock(1L, 1L, 50);

        assertEquals(150, inventory.getQuantity());
        verify(inventoryRepository).save(inventory);
    }

    @Test
    @DisplayName("Should remove stock successfully")
    void removeStock_Success() {
        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(inventory));

        inventoryService.removeStock(1L, 1L, 30);

        assertEquals(70, inventory.getQuantity());
        verify(inventoryRepository).save(inventory);
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when not enough stock")
    void removeStock_InsufficientStock() {
        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 1L))
                .thenReturn(Optional.of(inventory));

        // Available is 90, trying to remove 100
        assertThrows(InsufficientStockException.class,
                () -> inventoryService.removeStock(1L, 1L, 100));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid product/warehouse")
    void addStock_InvalidInventory() {
        when(inventoryRepository.findByProductIdAndWarehouseId(99L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> inventoryService.addStock(99L, 99L, 10));
    }
}
