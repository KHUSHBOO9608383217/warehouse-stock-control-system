package com.warehouse.stockmovement.controller;

import com.warehouse.stockmovement.dto.request.StockMovementRequest;
import com.warehouse.stockmovement.dto.response.ApiResponse;
import com.warehouse.stockmovement.dto.response.StockMovementResponse;
import com.warehouse.stockmovement.entity.MovementType;
import com.warehouse.stockmovement.service.StockMovementService;
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
@RequestMapping("/api/stock-movements")
@Tag(name = "Stock Movement", description = "APIs for recording stock movements (IN, OUT, TRANSFER)")
public class StockMovementController {

    private static final Logger log = LoggerFactory.getLogger(StockMovementController.class);
    private final StockMovementService stockMovementService;

    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @PostMapping
    @Operation(summary = "Create a stock movement",
            description = "Records a stock IN, OUT, or TRANSFER and updates inventory accordingly")
    public ResponseEntity<ApiResponse<StockMovementResponse>> createStockMovement(
            @Valid @RequestBody StockMovementRequest request) {
        log.info("REST request to create stock movement: {}", request.getMovementType());
        StockMovementResponse response = stockMovementService.createStockMovement(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Stock movement processed successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all stock movements")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> getAllStockMovements() {
        List<StockMovementResponse> movements = stockMovementService.getAllStockMovements();
        return ResponseEntity.ok(ApiResponse.success("Stock movements retrieved successfully", movements));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get stock movement by ID")
    public ResponseEntity<ApiResponse<StockMovementResponse>> getStockMovementById(
            @Parameter(description = "Stock Movement ID") @PathVariable Long id) {
        StockMovementResponse response = stockMovementService.getStockMovementById(id);
        return ResponseEntity.ok(ApiResponse.success("Stock movement retrieved successfully", response));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get stock movements by product ID")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> getStockMovementsByProductId(
            @Parameter(description = "Product ID") @PathVariable Long productId) {
        List<StockMovementResponse> movements = stockMovementService.getStockMovementsByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success("Stock movements retrieved successfully", movements));
    }

    @GetMapping("/warehouse/{warehouseId}")
    @Operation(summary = "Get stock movements by warehouse ID")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> getStockMovementsByWarehouseId(
            @Parameter(description = "Warehouse ID") @PathVariable Long warehouseId) {
        List<StockMovementResponse> movements = stockMovementService.getStockMovementsByWarehouseId(warehouseId);
        return ResponseEntity.ok(ApiResponse.success("Stock movements retrieved successfully", movements));
    }

    @GetMapping("/type/{movementType}")
    @Operation(summary = "Get stock movements by type (IN, OUT, TRANSFER)")
    public ResponseEntity<ApiResponse<List<StockMovementResponse>>> getStockMovementsByType(
            @Parameter(description = "Movement Type") @PathVariable MovementType movementType) {
        List<StockMovementResponse> movements = stockMovementService.getStockMovementsByType(movementType);
        return ResponseEntity.ok(ApiResponse.success("Stock movements retrieved successfully", movements));
    }
}
