package com.warehouse.inventory.controller;

import com.warehouse.inventory.dto.request.InventoryRequest;
import com.warehouse.inventory.dto.response.ApiResponse;
import com.warehouse.inventory.dto.response.InventoryResponse;
import com.warehouse.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory", description = "Inventory management APIs - tracks stock levels per product per warehouse")
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @Operation(summary = "Create a new inventory record")
    public ResponseEntity<ApiResponse<InventoryResponse>> createInventory(
            @Valid @RequestBody InventoryRequest request) {
        log.info("REST request to create inventory for productId: {}", request.getProductId());
        InventoryResponse response = inventoryService.createInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Inventory created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all inventory records")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAllInventory() {
        List<InventoryResponse> inventory = inventoryService.getAllInventory();
        return ResponseEntity.ok(ApiResponse.success("Inventory retrieved successfully", inventory));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inventory by ID")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventoryById(
            @Parameter(description = "Inventory ID") @PathVariable Long id) {
        InventoryResponse response = inventoryService.getInventoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Inventory retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an inventory record")
    public ResponseEntity<ApiResponse<InventoryResponse>> updateInventory(
            @Parameter(description = "Inventory ID") @PathVariable Long id,
            @Valid @RequestBody InventoryRequest request) {
        InventoryResponse response = inventoryService.updateInventory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Inventory updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an inventory record")
    public ResponseEntity<ApiResponse<Void>> deleteInventory(
            @Parameter(description = "Inventory ID") @PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.ok(ApiResponse.success("Inventory deleted successfully"));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get inventory by product ID")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventoryByProductId(
            @Parameter(description = "Product ID") @PathVariable Long productId) {
        List<InventoryResponse> inventory = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success("Inventory retrieved successfully", inventory));
    }

    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "Get inventory by warehouse ID")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventoryByWarehouseId(
            @Parameter(description = "Warehouse ID") @PathVariable Long warehouseId) {
        List<InventoryResponse> inventory = inventoryService.getInventoryByWarehouseId(warehouseId);
        return ResponseEntity.ok(ApiResponse.success("Inventory retrieved successfully", inventory));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low-stock inventory",
            description = "Returns inventory records where quantity is at or below the product's minimum stock level")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getLowStockInventory() {
        List<InventoryResponse> inventory = inventoryService.getLowStockInventory();
        return ResponseEntity.ok(ApiResponse.success("Low-stock inventory retrieved successfully", inventory));
    }

    @PutMapping("/add-stock")
    @Operation(summary = "Add stock to inventory (used internally by Stock Movement Service)")
    public ResponseEntity<ApiResponse<Void>> addStock(
            @RequestParam Long productId,
            @RequestParam Long warehouseId,
            @RequestParam int quantity) {
        inventoryService.addStock(productId, warehouseId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Stock added successfully"));
    }

    @PutMapping("/remove-stock")
    @Operation(summary = "Remove stock from inventory (used internally by Stock Movement Service)")
    public ResponseEntity<ApiResponse<Void>> removeStock(
            @RequestParam Long productId,
            @RequestParam Long warehouseId,
            @RequestParam int quantity) {
        inventoryService.removeStock(productId, warehouseId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Stock removed successfully"));
    }
}
