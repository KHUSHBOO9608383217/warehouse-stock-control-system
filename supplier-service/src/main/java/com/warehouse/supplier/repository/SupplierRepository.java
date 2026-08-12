package com.warehouse.supplier.repository;

import com.warehouse.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findBySupplierCode(String supplierCode);

    boolean existsBySupplierCode(String supplierCode);

    List<Supplier> findByNameContainingIgnoreCase(String name);

    List<Supplier> findByActiveTrue();
}
