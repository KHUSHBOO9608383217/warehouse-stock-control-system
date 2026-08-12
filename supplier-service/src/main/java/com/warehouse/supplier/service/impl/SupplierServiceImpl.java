package com.warehouse.supplier.service.impl;

import com.warehouse.supplier.dto.request.SupplierRequest;
import com.warehouse.supplier.dto.response.SupplierResponse;
import com.warehouse.supplier.entity.Supplier;
import com.warehouse.supplier.exception.DuplicateResourceException;
import com.warehouse.supplier.exception.ResourceNotFoundException;
import com.warehouse.supplier.mapper.SupplierMapper;
import com.warehouse.supplier.repository.SupplierRepository;
import com.warehouse.supplier.service.SupplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private static final Logger log = LoggerFactory.getLogger(SupplierServiceImpl.class);

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierServiceImpl(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    @Override
    public SupplierResponse createSupplier(SupplierRequest request) {
        log.info("Creating supplier with code: {}", request.getSupplierCode());

        if (supplierRepository.existsBySupplierCode(request.getSupplierCode())) {
            throw new DuplicateResourceException("Supplier", "supplierCode", request.getSupplierCode());
        }

        Supplier supplier = supplierMapper.toEntity(request);
        Supplier saved = supplierRepository.save(supplier);
        log.info("Supplier created successfully with id: {}", saved.getId());
        return supplierMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {
        log.info("Fetching supplier with id: {}", id);
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        return supplierMapper.toResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers() {
        log.info("Fetching all suppliers");
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        log.info("Updating supplier with id: {}", id);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));

        supplierRepository.findBySupplierCode(request.getSupplierCode())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException("Supplier", "supplierCode", request.getSupplierCode());
                    }
                });

        supplierMapper.updateEntity(supplier, request);
        Supplier updated = supplierRepository.save(supplier);
        log.info("Supplier updated successfully with id: {}", updated.getId());
        return supplierMapper.toResponse(updated);
    }

    @Override
    public void deleteSupplier(Long id) {
        log.info("Deactivating supplier with id: {}", id);
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        supplier.setActive(false);
        supplierRepository.save(supplier);
        log.info("Supplier deactivated successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> searchSuppliersByName(String name) {
        log.info("Searching suppliers by name: {}", name);
        return supplierRepository.findByNameContainingIgnoreCase(name).stream()
                .map(supplierMapper::toResponse)
                .collect(Collectors.toList());
    }
}
