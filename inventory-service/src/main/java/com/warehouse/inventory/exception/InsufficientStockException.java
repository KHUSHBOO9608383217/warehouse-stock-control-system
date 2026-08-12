package com.warehouse.inventory.exception;

/**
 * Thrown when a stock operation cannot be completed due to insufficient stock.
 * Results in HTTP 400 BAD REQUEST.
 */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
