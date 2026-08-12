package com.warehouse.warehouseservice.service.impl;

import com.warehouse.warehouseservice.dto.request.WarehouseRequest;
import com.warehouse.warehouseservice.dto.response.WarehouseResponse;
import com.warehouse.warehouseservice.entity.Warehouse;
import com.warehouse.warehouseservice.exception.DuplicateResourceException;
import com.warehouse.warehouseservice.exception.ResourceNotFoundException;
import com.warehouse.warehouseservice.mapper.WarehouseMapper;
import com.warehouse.warehouseservice.repository.WarehouseRepository;
import com.warehouse.warehouseservice.service.WarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private static final Logger log = LoggerFactory.getLogger(WarehouseServiceImpl.class);

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
    }

    @Override
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        log.info("Creating warehouse with code: {}", request.getWarehouseCode());

        if (warehouseRepository.existsByWarehouseCode(request.getWarehouseCode())) {
            throw new DuplicateResourceException("Warehouse", "warehouseCode", request.getWarehouseCode());
        }

        Warehouse warehouse = warehouseMapper.toEntity(request);
        Warehouse saved = warehouseRepository.save(warehouse);
        log.info("Warehouse created successfully with id: {}", saved.getId());
        return warehouseMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouseById(Long id) {
        log.info("Fetching warehouse with id: {}", id);
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        return warehouseMapper.toResponse(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseResponse> getAllWarehouses() {
        log.info("Fetching all warehouses");
        return warehouseRepository.findAll().stream()
                .map(warehouseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {
        log.info("Updating warehouse with id: {}", id);

        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));

        warehouseRepository.findByWarehouseCode(request.getWarehouseCode())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException("Warehouse", "warehouseCode", request.getWarehouseCode());
                    }
                });

        warehouseMapper.updateEntity(warehouse, request);
        Warehouse updated = warehouseRepository.save(warehouse);
        log.info("Warehouse updated successfully with id: {}", updated.getId());
        return warehouseMapper.toResponse(updated);
    }

    @Override
    public void deleteWarehouse(Long id) {
        log.info("Deactivating warehouse with id: {}", id);
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        warehouse.setActive(false);
        warehouseRepository.save(warehouse);
        log.info("Warehouse deactivated successfully with id: {}", id);
    }
}
