package com.iwfc.exception;

/**
 * [Exception Handling] Invalid booking rule.
 * Checked exception (extends Exception), so the compiler makes the caller handle it.
 *
 * Checked exception: callers of SessionService.bookSession() are forced to
 * handle a double-booking or an out-of-operating-hours booking attempt.
 */
public class InvalidBookingException extends Exception {

    public InvalidBookingException(String message) {
        super(message);
    }
}
