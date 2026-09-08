package com.iwfc.exception;

/**
 * Thrown when a caller without the required role attempts an
 * administrator-only action (e.g. a Member viewing the global maintenance log).
 */
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
