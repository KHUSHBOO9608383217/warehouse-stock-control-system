package com.warehouse.warehouseservice.service;

import com.warehouse.warehouseservice.dto.request.WarehouseRequest;
import com.warehouse.warehouseservice.dto.response.WarehouseResponse;

import java.util.List;

public interface WarehouseService {

    WarehouseResponse createWarehouse(WarehouseRequest request);

    WarehouseResponse getWarehouseById(Long id);

    List<WarehouseResponse> getAllWarehouses();

    WarehouseResponse updateWarehouse(Long id, WarehouseRequest request);

    void deleteWarehouse(Long id);
}
