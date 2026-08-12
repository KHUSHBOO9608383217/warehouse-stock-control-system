package com.warehouse.supplier.service;

import com.warehouse.supplier.dto.request.SupplierRequest;
import com.warehouse.supplier.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {

    SupplierResponse createSupplier(SupplierRequest request);

    SupplierResponse getSupplierById(Long id);

    List<SupplierResponse> getAllSuppliers();

    SupplierResponse updateSupplier(Long id, SupplierRequest request);

    void deleteSupplier(Long id);

    List<SupplierResponse> searchSuppliersByName(String name);
}
