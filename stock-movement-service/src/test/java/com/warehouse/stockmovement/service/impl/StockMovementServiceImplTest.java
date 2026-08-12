package com.warehouse.stockmovement.service.impl;

import com.warehouse.stockmovement.client.InventoryServiceClient;
import com.warehouse.stockmovement.client.ProductServiceClient;
import com.warehouse.stockmovement.dto.request.StockMovementRequest;
import com.warehouse.stockmovement.dto.response.StockMovementResponse;
import com.warehouse.stockmovement.entity.MovementType;
import com.warehouse.stockmovement.entity.StockMovement;
import com.warehouse.stockmovement.exception.InvalidStockMovementException;
import com.warehouse.stockmovement.kafka.StockEventProducer;
import com.warehouse.stockmovement.mapper.StockMovementMapper;
import com.warehouse.stockmovement.repository.StockMovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockMovementServiceImpl Unit Tests")
class StockMovementServiceImplTest {

    @Mock
    private StockMovementRepository stockMovementRepository;
    @Mock
    private StockMovementMapper stockMovementMapper;
    @Mock
    private InventoryServiceClient inventoryServiceClient;
    @Mock
    private ProductServiceClient productServiceClient;
    @Mock
    private StockEventProducer stockEventProducer;

    @InjectMocks
    private StockMovementServiceImpl stockMovementService;

    private StockMovement stockMovement;
    private StockMovementResponse stockMovementResponse;

    @BeforeEach
    void setUp() {
        stockMovement = StockMovement.builder()
                .id(1L).productId(1L).warehouseId(1L)
                .quantity(100).movementType(MovementType.IN)
                .referenceNumber("REF-001").build();

        stockMovementResponse = StockMovementResponse.builder()
                .id(1L).productId(1L).warehouseId(1L)
                .quantity(100).movementType(MovementType.IN)
                .referenceNumber("REF-001").build();
    }

    @Test
    @DisplayName("Should process STOCK IN successfully")
    void stockIn_Success() {
        StockMovementRequest request = StockMovementRequest.builder()
                .productId(1L).warehouseId(1L).quantity(100)
                .movementType(MovementType.IN).referenceNumber("REF-001").build();

        when(productServiceClient.isProductActive(1L)).thenReturn(true);
        when(stockMovementMapper.toEntity(request)).thenReturn(stockMovement);
        when(stockMovementRepository.save(any())).thenReturn(stockMovement);
        when(stockMovementMapper.toResponse(stockMovement)).thenReturn(stockMovementResponse);

        StockMovementResponse result = stockMovementService.createStockMovement(request);

        assertNotNull(result);
        assertEquals(MovementType.IN, result.getMovementType());
        verify(inventoryServiceClient).addStock(1L, 1L, 100);
        verify(stockEventProducer).publishStockEvent(any());
    }

    @Test
    @DisplayName("Should process STOCK OUT successfully")
    void stockOut_Success() {
        StockMovementRequest request = StockMovementRequest.builder()
                .productId(1L).warehouseId(1L).quantity(30)
                .movementType(MovementType.OUT).referenceNumber("REF-002").build();

        StockMovement outMovement = StockMovement.builder()
                .id(2L).productId(1L).warehouseId(1L)
                .quantity(30).movementType(MovementType.OUT).build();
        StockMovementResponse outResponse = StockMovementResponse.builder()
                .id(2L).quantity(30).movementType(MovementType.OUT).build();

        when(productServiceClient.isProductActive(1L)).thenReturn(true);
        when(stockMovementMapper.toEntity(request)).thenReturn(outMovement);
        when(stockMovementRepository.save(any())).thenReturn(outMovement);
        when(stockMovementMapper.toResponse(outMovement)).thenReturn(outResponse);

        StockMovementResponse result = stockMovementService.createStockMovement(request);

        assertNotNull(result);
        verify(inventoryServiceClient).removeStock(1L, 1L, 30);
    }

    @Test
    @DisplayName("Should reject STOCK OUT for inactive product")
    void stockOut_InactiveProduct() {
        StockMovementRequest request = StockMovementRequest.builder()
                .productId(1L).warehouseId(1L).quantity(10)
                .movementType(MovementType.OUT).build();

        when(productServiceClient.isProductActive(1L)).thenReturn(false);

        assertThrows(InvalidStockMovementException.class,
                () -> stockMovementService.createStockMovement(request));
    }

    @Test
    @DisplayName("Should process STOCK TRANSFER successfully")
    void stockTransfer_Success() {
        StockMovementRequest request = StockMovementRequest.builder()
                .productId(1L).warehouseId(1L).destinationWarehouseId(2L)
                .quantity(20).movementType(MovementType.TRANSFER)
                .referenceNumber("TRF-001").build();

        StockMovement transferMovement = StockMovement.builder()
                .id(3L).productId(1L).warehouseId(1L).destinationWarehouseId(2L)
                .quantity(20).movementType(MovementType.TRANSFER).build();
        StockMovementResponse transferResponse = StockMovementResponse.builder()
                .id(3L).quantity(20).movementType(MovementType.TRANSFER).build();

        when(productServiceClient.isProductActive(1L)).thenReturn(true);
        when(stockMovementMapper.toEntity(request)).thenReturn(transferMovement);
        when(stockMovementRepository.save(any())).thenReturn(transferMovement);
        when(stockMovementMapper.toResponse(transferMovement)).thenReturn(transferResponse);

        StockMovementResponse result = stockMovementService.createStockMovement(request);

        assertNotNull(result);
        verify(inventoryServiceClient).removeStock(1L, 1L, 20);
        verify(inventoryServiceClient).addStock(1L, 2L, 20);
    }

    @Test
    @DisplayName("Should reject TRANSFER without destination warehouse")
    void stockTransfer_NoDestination() {
        StockMovementRequest request = StockMovementRequest.builder()
                .productId(1L).warehouseId(1L)
                .quantity(20).movementType(MovementType.TRANSFER).build();

        when(productServiceClient.isProductActive(1L)).thenReturn(true);

        assertThrows(InvalidStockMovementException.class,
                () -> stockMovementService.createStockMovement(request));
    }

    @Test
    @DisplayName("Should reject TRANSFER with same source and destination")
    void stockTransfer_SameWarehouse() {
        StockMovementRequest request = StockMovementRequest.builder()
                .productId(1L).warehouseId(1L).destinationWarehouseId(1L)
                .quantity(20).movementType(MovementType.TRANSFER).build();

        when(productServiceClient.isProductActive(1L)).thenReturn(true);

        assertThrows(InvalidStockMovementException.class,
                () -> stockMovementService.createStockMovement(request));
    }
}
