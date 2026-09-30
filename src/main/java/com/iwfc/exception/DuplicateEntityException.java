package com.iwfc.exception;

/**
 * [Exception Handling] Duplicate data rule.
 * Unchecked (extends RuntimeException): a duplicate ID is treated as a caller error,
 * so callers are not forced to catch it.
 *
 * Thrown when an entity is registered with an ID/username that already
 * exists (e.g. equipment ID clash, duplicate username).
 */
public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String message) {
        super(message);
    }
}
