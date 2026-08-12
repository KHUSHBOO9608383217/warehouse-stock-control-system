package com.warehouse.inventory.service.impl;

import com.warehouse.inventory.client.ProductServiceClient;
import com.warehouse.inventory.dto.ProductDTO;
import com.warehouse.inventory.dto.request.InventoryRequest;
import com.warehouse.inventory.dto.response.InventoryResponse;
import com.warehouse.inventory.entity.Inventory;
import com.warehouse.inventory.exception.InsufficientStockException;
import com.warehouse.inventory.exception.ResourceNotFoundException;
import com.warehouse.inventory.mapper.InventoryMapper;
import com.warehouse.inventory.repository.InventoryRepository;
import com.warehouse.inventory.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductServiceClient productServiceClient;

    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                InventoryMapper inventoryMapper,
                                ProductServiceClient productServiceClient) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
        this.productServiceClient = productServiceClient;
    }

    @Override
    public InventoryResponse createInventory(InventoryRequest request) {
        log.info("Creating inventory for productId: {} in warehouseId: {}",
                request.getProductId(), request.getWarehouseId());

        // Validate: reserved quantity cannot exceed total quantity
        if (request.getReservedQuantity() != null && request.getReservedQuantity() > request.getQuantity()) {
            throw new IllegalArgumentException("Reserved quantity cannot exceed total quantity");
        }

        // Check for duplicate inventory record
        if (inventoryRepository.existsByProductIdAndWarehouseId(request.getProductId(), request.getWarehouseId())) {
            throw new IllegalArgumentException(String.format(
                    "Inventory already exists for productId: %d and warehouseId: %d",
                    request.getProductId(), request.getWarehouseId()));
        }

        Inventory inventory = inventoryMapper.toEntity(request);
        Inventory saved = inventoryRepository.save(inventory);
        log.info("Inventory created successfully with id: {}", saved.getId());
        return inventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryById(Long id) {
        log.info("Fetching inventory with id: {}", id);
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));
        return inventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventory() {
        log.info("Fetching all inventory records");
        return inventoryRepository.findAll().stream()
                .map(inventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryResponse updateInventory(Long id, InventoryRequest request) {
        log.info("Updating inventory with id: {}", id);

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));

        if (request.getReservedQuantity() != null && request.getReservedQuantity() > request.getQuantity()) {
            throw new IllegalArgumentException("Reserved quantity cannot exceed total quantity");
        }

        inventory.setProductId(request.getProductId());
        inventory.setWarehouseId(request.getWarehouseId());
        inventory.setQuantity(request.getQuantity());
        inventory.setReservedQuantity(request.getReservedQuantity() != null ? request.getReservedQuantity() : 0);
        inventory.recalculateAvailableQuantity();

        Inventory updated = inventoryRepository.save(inventory);
        log.info("Inventory updated successfully with id: {}", updated.getId());
        return inventoryMapper.toResponse(updated);
    }

    @Override
    public void deleteInventory(Long id) {
        log.info("Deleting inventory with id: {}", id);
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));
        inventoryRepository.delete(inventory);
        log.info("Inventory deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryByProductId(Long productId) {
        log.info("Fetching inventory for productId: {}", productId);
        return inventoryRepository.findByProductId(productId).stream()
                .map(inventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryByWarehouseId(Long warehouseId) {
        log.info("Fetching inventory for warehouseId: {}", warehouseId);
        return inventoryRepository.findByWarehouseId(warehouseId).stream()
                .map(inventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getLowStockInventory() {
        log.info("Fetching low-stock inventory");

        // Get all inventory records and check each against its product's minimum stock level
        return inventoryRepository.findAll().stream()
                .filter(inventory -> {
                    ProductDTO product = productServiceClient.getProductById(inventory.getProductId());
                    if (product != null && product.getMinimumStockLevel() != null) {
                        return inventory.getQuantity() <= product.getMinimumStockLevel();
                    }
                    return false;
                })
                .map(inventoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void addStock(Long productId, Long warehouseId, int quantity) {
        log.info("Adding {} units of stock for productId: {} in warehouseId: {}",
                quantity, productId, warehouseId);

        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Inventory not found for productId: %d and warehouseId: %d",
                                productId, warehouseId)));

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.recalculateAvailableQuantity();
        inventoryRepository.save(inventory);

        log.info("Stock added successfully. New quantity: {}", inventory.getQuantity());
    }

    @Override
    public void removeStock(Long productId, Long warehouseId, int quantity) {
        log.info("Removing {} units of stock for productId: {} in warehouseId: {}",
                quantity, productId, warehouseId);

        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Inventory not found for productId: %d and warehouseId: %d",
                                productId, warehouseId)));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException(String.format(
                    "Insufficient stock. Available: %d, Requested: %d",
                    inventory.getAvailableQuantity(), quantity));
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventory.recalculateAvailableQuantity();
        inventoryRepository.save(inventory);

        log.info("Stock removed successfully. New quantity: {}", inventory.getQuantity());
    }
}
