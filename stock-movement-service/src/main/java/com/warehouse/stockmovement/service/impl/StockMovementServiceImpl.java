package com.warehouse.stockmovement.service.impl;

import com.warehouse.stockmovement.client.InventoryServiceClient;
import com.warehouse.stockmovement.client.ProductServiceClient;
import com.warehouse.stockmovement.dto.event.StockEvent;
import com.warehouse.stockmovement.dto.request.StockMovementRequest;
import com.warehouse.stockmovement.dto.response.StockMovementResponse;
import com.warehouse.stockmovement.entity.MovementType;
import com.warehouse.stockmovement.entity.StockMovement;
import com.warehouse.stockmovement.exception.InvalidStockMovementException;
import com.warehouse.stockmovement.exception.ResourceNotFoundException;
import com.warehouse.stockmovement.kafka.StockEventProducer;
import com.warehouse.stockmovement.mapper.StockMovementMapper;
import com.warehouse.stockmovement.repository.StockMovementRepository;
import com.warehouse.stockmovement.service.StockMovementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StockMovementServiceImpl implements StockMovementService {

    private static final Logger log = LoggerFactory.getLogger(StockMovementServiceImpl.class);

    private final StockMovementRepository stockMovementRepository;
    private final StockMovementMapper stockMovementMapper;
    private final InventoryServiceClient inventoryServiceClient;
    private final ProductServiceClient productServiceClient;
    private final StockEventProducer stockEventProducer;

    public StockMovementServiceImpl(StockMovementRepository stockMovementRepository,
                                    StockMovementMapper stockMovementMapper,
                                    InventoryServiceClient inventoryServiceClient,
                                    ProductServiceClient productServiceClient,
                                    StockEventProducer stockEventProducer) {
        this.stockMovementRepository = stockMovementRepository;
        this.stockMovementMapper = stockMovementMapper;
        this.inventoryServiceClient = inventoryServiceClient;
        this.productServiceClient = productServiceClient;
        this.stockEventProducer = stockEventProducer;
    }

    @Override
    public StockMovementResponse createStockMovement(StockMovementRequest request) {
        log.info("Processing stock movement: type={}, productId={}, warehouseId={}, quantity={}",
                request.getMovementType(), request.getProductId(),
                request.getWarehouseId(), request.getQuantity());

        // Validate the movement request
        validateMovement(request);

        // Process the movement based on type
        switch (request.getMovementType()) {
            case IN:
                processStockIn(request);
                break;
            case OUT:
                processStockOut(request);
                break;
            case TRANSFER:
                processStockTransfer(request);
                break;
            default:
                throw new InvalidStockMovementException("Unknown movement type: " + request.getMovementType());
        }

        // Save the movement record
        StockMovement movement = stockMovementMapper.toEntity(request);
        StockMovement saved = stockMovementRepository.save(movement);
        log.info("Stock movement recorded with id: {}", saved.getId());

        // Check for low stock and publish event if needed
        checkAndPublishLowStockEvent(request.getProductId(), request.getWarehouseId());

        return stockMovementMapper.toResponse(saved);
    }

    private void validateMovement(StockMovementRequest request) {
        // Check if product is active
        if (!productServiceClient.isProductActive(request.getProductId())) {
            throw new InvalidStockMovementException(
                    "Cannot process stock movement for inactive product: " + request.getProductId());
        }

        // Validate TRANSFER has destination warehouse
        if (request.getMovementType() == MovementType.TRANSFER) {
            if (request.getDestinationWarehouseId() == null) {
                throw new InvalidStockMovementException(
                        "Destination warehouse ID is required for TRANSFER movements");
            }
            if (request.getWarehouseId().equals(request.getDestinationWarehouseId())) {
                throw new InvalidStockMovementException(
                        "Source and destination warehouse cannot be the same");
            }
        }
    }

    private void processStockIn(StockMovementRequest request) {
        log.info("Processing STOCK IN: {} units of productId {} into warehouseId {}",
                request.getQuantity(), request.getProductId(), request.getWarehouseId());

        // Call Inventory Service to add stock
        inventoryServiceClient.addStock(request.getProductId(), request.getWarehouseId(), request.getQuantity());

        // Publish StockReceivedEvent to Kafka
        StockEvent event = StockEvent.builder()
                .eventType("STOCK_RECEIVED")
                .productId(request.getProductId())
                .warehouseId(request.getWarehouseId())
                .quantity(request.getQuantity())
                .referenceNumber(request.getReferenceNumber())
                .message(String.format("Received %d units of product %d into warehouse %d",
                        request.getQuantity(), request.getProductId(), request.getWarehouseId()))
                .timestamp(LocalDateTime.now())
                .build();
        stockEventProducer.publishStockEvent(event);
    }

    private void processStockOut(StockMovementRequest request) {
        log.info("Processing STOCK OUT: {} units of productId {} from warehouseId {}",
                request.getQuantity(), request.getProductId(), request.getWarehouseId());

        // Call Inventory Service to remove stock (will throw if insufficient)
        inventoryServiceClient.removeStock(request.getProductId(), request.getWarehouseId(), request.getQuantity());

        // Publish StockIssuedEvent to Kafka
        StockEvent event = StockEvent.builder()
                .eventType("STOCK_ISSUED")
                .productId(request.getProductId())
                .warehouseId(request.getWarehouseId())
                .quantity(request.getQuantity())
                .referenceNumber(request.getReferenceNumber())
                .message(String.format("Issued %d units of product %d from warehouse %d",
                        request.getQuantity(), request.getProductId(), request.getWarehouseId()))
                .timestamp(LocalDateTime.now())
                .build();
        stockEventProducer.publishStockEvent(event);
    }

    private void processStockTransfer(StockMovementRequest request) {
        log.info("Processing STOCK TRANSFER: {} units of productId {} from warehouseId {} to warehouseId {}",
                request.getQuantity(), request.getProductId(),
                request.getWarehouseId(), request.getDestinationWarehouseId());

        // Remove from source warehouse
        inventoryServiceClient.removeStock(request.getProductId(), request.getWarehouseId(), request.getQuantity());

        // Add to destination warehouse
        inventoryServiceClient.addStock(request.getProductId(), request.getDestinationWarehouseId(), request.getQuantity());

        // Publish StockTransferredEvent to Kafka
        StockEvent event = StockEvent.builder()
                .eventType("STOCK_TRANSFERRED")
                .productId(request.getProductId())
                .warehouseId(request.getWarehouseId())
                .destinationWarehouseId(request.getDestinationWarehouseId())
                .quantity(request.getQuantity())
                .referenceNumber(request.getReferenceNumber())
                .message(String.format("Transferred %d units of product %d from warehouse %d to warehouse %d",
                        request.getQuantity(), request.getProductId(),
                        request.getWarehouseId(), request.getDestinationWarehouseId()))
                .timestamp(LocalDateTime.now())
                .build();
        stockEventProducer.publishStockEvent(event);
    }

    /**
     * Checks if the current stock level is at or below the minimum stock level.
     * If so, publishes a LowStockEvent to Kafka.
     */
    private void checkAndPublishLowStockEvent(Long productId, Long warehouseId) {
        try {
            int currentStock = inventoryServiceClient.getCurrentStock(productId, warehouseId);
            int minimumStockLevel = productServiceClient.getMinimumStockLevel(productId);

            if (minimumStockLevel > 0 && currentStock <= minimumStockLevel) {
                log.warn("LOW STOCK DETECTED: productId={}, warehouseId={}, currentStock={}, minimumLevel={}",
                        productId, warehouseId, currentStock, minimumStockLevel);

                StockEvent lowStockEvent = StockEvent.builder()
                        .eventType("LOW_STOCK")
                        .productId(productId)
                        .warehouseId(warehouseId)
                        .currentStock(currentStock)
                        .minimumStockLevel(minimumStockLevel)
                        .message(String.format(
                                "LOW STOCK ALERT: Product %d in Warehouse %d. Current: %d, Minimum: %d",
                                productId, warehouseId, currentStock, minimumStockLevel))
                        .timestamp(LocalDateTime.now())
                        .build();
                stockEventProducer.publishLowStockEvent(lowStockEvent);
            }
        } catch (Exception e) {
            log.warn("Could not check low-stock status: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public StockMovementResponse getStockMovementById(Long id) {
        StockMovement movement = stockMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockMovement", "id", id));
        return stockMovementMapper.toResponse(movement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponse> getAllStockMovements() {
        return stockMovementRepository.findAll().stream()
                .map(stockMovementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponse> getStockMovementsByProductId(Long productId) {
        return stockMovementRepository.findByProductId(productId).stream()
                .map(stockMovementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponse> getStockMovementsByWarehouseId(Long warehouseId) {
        return stockMovementRepository.findByWarehouseId(warehouseId).stream()
                .map(stockMovementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMovementResponse> getStockMovementsByType(MovementType movementType) {
        return stockMovementRepository.findByMovementType(movementType).stream()
                .map(stockMovementMapper::toResponse)
                .collect(Collectors.toList());
    }
}
