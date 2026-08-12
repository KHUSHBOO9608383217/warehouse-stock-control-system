package com.warehouse.supplier.controller;

import com.warehouse.supplier.dto.request.SupplierRequest;
import com.warehouse.supplier.dto.response.ApiResponse;
import com.warehouse.supplier.dto.response.SupplierResponse;
import com.warehouse.supplier.service.SupplierService;
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
@RequestMapping("/api/suppliers")
@Tag(name = "Supplier", description = "Supplier management APIs")
public class SupplierController {

    private static final Logger log = LoggerFactory.getLogger(SupplierController.class);
    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    @Operation(summary = "Create a new supplier")
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(@Valid @RequestBody SupplierRequest request) {
        log.info("REST request to create supplier: {}", request.getSupplierCode());
        SupplierResponse response = supplierService.createSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Supplier created successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all suppliers")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> getAllSuppliers() {
        List<SupplierResponse> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(ApiResponse.success("Suppliers retrieved successfully", suppliers));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplierById(
            @Parameter(description = "Supplier ID") @PathVariable Long id) {
        SupplierResponse response = supplierService.getSupplierById(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a supplier")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @Parameter(description = "Supplier ID") @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.updateSupplier(id, request);
        return ResponseEntity.ok(ApiResponse.success("Supplier updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete (deactivate) a supplier")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(
            @Parameter(description = "Supplier ID") @PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier deactivated successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search suppliers by name")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> searchSuppliers(
            @Parameter(description = "Supplier name to search") @RequestParam String name) {
        List<SupplierResponse> suppliers = supplierService.searchSuppliersByName(name);
        return ResponseEntity.ok(ApiResponse.success("Suppliers retrieved successfully", suppliers));
    }
}
