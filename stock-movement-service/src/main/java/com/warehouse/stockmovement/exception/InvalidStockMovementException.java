package com.warehouse.stockmovement.exception;

/**
 * Thrown when a stock movement request is invalid.
 * For example: TRANSFER without a destination warehouse,
 * or insufficient stock for STOCK OUT.
 */
public class InvalidStockMovementException extends RuntimeException {
    public InvalidStockMovementException(String message) {
        super(message);
    }
}
