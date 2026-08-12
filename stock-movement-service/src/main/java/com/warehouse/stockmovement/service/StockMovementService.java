package com.warehouse.stockmovement.service;

import com.warehouse.stockmovement.dto.request.StockMovementRequest;
import com.warehouse.stockmovement.dto.response.StockMovementResponse;
import com.warehouse.stockmovement.entity.MovementType;

import java.util.List;

public interface StockMovementService {

    StockMovementResponse createStockMovement(StockMovementRequest request);

    StockMovementResponse getStockMovementById(Long id);

    List<StockMovementResponse> getAllStockMovements();

    List<StockMovementResponse> getStockMovementsByProductId(Long productId);

    List<StockMovementResponse> getStockMovementsByWarehouseId(Long warehouseId);

    List<StockMovementResponse> getStockMovementsByType(MovementType movementType);
}
