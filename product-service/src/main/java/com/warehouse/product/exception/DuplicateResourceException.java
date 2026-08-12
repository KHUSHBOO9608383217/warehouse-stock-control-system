package com.warehouse.product.exception;

/**
 * Thrown when attempting to create a resource that already exists.
 * For example, creating a product with a duplicate product code.
 * Results in HTTP 409 CONFLICT.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
