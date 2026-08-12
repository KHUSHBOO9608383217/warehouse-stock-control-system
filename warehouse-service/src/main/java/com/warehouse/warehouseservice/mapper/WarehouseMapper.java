package com.warehouse.warehouseservice.mapper;

import com.warehouse.warehouseservice.dto.request.WarehouseRequest;
import com.warehouse.warehouseservice.dto.response.WarehouseResponse;
import com.warehouse.warehouseservice.entity.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    public Warehouse toEntity(WarehouseRequest request) {
        return Warehouse.builder()
                .warehouseCode(request.getWarehouseCode())
                .name(request.getName())
                .location(request.getLocation())
                .managerName(request.getManagerName())
                .capacity(request.getCapacity())
                .active(true)
                .build();
    }

    public WarehouseResponse toResponse(Warehouse warehouse) {
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .warehouseCode(warehouse.getWarehouseCode())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .managerName(warehouse.getManagerName())
                .capacity(warehouse.getCapacity())
                .active(warehouse.getActive())
                .createdAt(warehouse.getCreatedAt())
                .updatedAt(warehouse.getUpdatedAt())
                .build();
    }

    public void updateEntity(Warehouse warehouse, WarehouseRequest request) {
        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        warehouse.setManagerName(request.getManagerName());
        warehouse.setCapacity(request.getCapacity());
    }
}
