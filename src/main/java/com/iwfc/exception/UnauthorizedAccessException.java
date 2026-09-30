package com.iwfc.exception;

/**
 * [Exception Handling] Unauthorized access rule (role based access control).
 * Unchecked (extends RuntimeException): a permission failure is a policy violation,
 * not something the caller can normally recover from.
 *
 * Thrown when a caller without the required role attempts an
 * administrator-only action (e.g. a Member viewing the global maintenance log).
 */
public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
