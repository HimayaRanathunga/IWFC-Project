package com.iwfc.exception;

/**
 * Thrown when an entity is registered with an ID/username that already
 * exists (e.g. equipment ID clash, duplicate username).
 */
public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String message) {
        super(message);
    }
}
