package com.iwfc.exception;

/**
 * Checked exception: callers of SessionService.bookSession() are forced to
 * handle a double-booking or an out-of-operating-hours booking attempt.
 */
public class InvalidBookingException extends Exception {

    public InvalidBookingException(String message) {
        super(message);
    }
}
