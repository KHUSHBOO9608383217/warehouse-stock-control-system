package com.warehouse.warehouseservice.controller;

import com.warehouse.warehouseservice.dto.request.WarehouseRequest;
import com.warehouse.warehouseservice.dto.response.ApiResponse;
import com.warehouse.warehouseservice.dto.response.WarehouseResponse;
import com.warehouse.warehouseservice.service.WarehouseService;
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
@RequestMapping("/api/warehouses")
@Tag(name = "Warehouse", description = "Warehouse management APIs")
public class WarehouseController {

    private static final Logger log = LoggerFactory.getLogger(WarehouseController.class);
    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping
    @Operation(summary = "Create a new warehouse")
    public ResponseEntity<ApiResponse<WarehouseResponse>> createWarehouse(@Valid @RequestBody WarehouseRequest request) {
        log.info("REST request to create warehouse: {}", request.getWarehouseCode());
        WarehouseResponse response = warehouseService.createWarehouse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Warehouse created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all warehouses")
    public ResponseEntity<ApiResponse<List<WarehouseResponse>>> getAllWarehouses() {
        log.info("REST request to get all warehouses");
        List<WarehouseResponse> warehouses = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(ApiResponse.success("Warehouses retrieved successfully", warehouses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get warehouse by ID")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouseById(
            @Parameter(description = "Warehouse ID") @PathVariable Long id) {
        log.info("REST request to get warehouse by id: {}", id);
        WarehouseResponse response = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(ApiResponse.success("Warehouse retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a warehouse")
    public ResponseEntity<ApiResponse<WarehouseResponse>> updateWarehouse(
            @Parameter(description = "Warehouse ID") @PathVariable Long id,
            @Valid @RequestBody WarehouseRequest request) {
        log.info("REST request to update warehouse with id: {}", id);
        WarehouseResponse response = warehouseService.updateWarehouse(id, request);
        return ResponseEntity.ok(ApiResponse.success("Warehouse updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete (deactivate) a warehouse")
    public ResponseEntity<ApiResponse<Void>> deleteWarehouse(
            @Parameter(description = "Warehouse ID") @PathVariable Long id) {
        log.info("REST request to delete warehouse with id: {}", id);
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.ok(ApiResponse.success("Warehouse deactivated successfully"));
    }
}
