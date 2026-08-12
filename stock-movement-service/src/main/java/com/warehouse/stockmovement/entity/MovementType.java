package com.warehouse.stockmovement.entity;

/**
 * Type of stock movement.
 * IN - stock received into warehouse
 * OUT - stock issued from warehouse
 * TRANSFER - stock moved between warehouses
 */
public enum MovementType {
    IN,
    OUT,
    TRANSFER
}
