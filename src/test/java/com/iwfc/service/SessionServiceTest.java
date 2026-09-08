package com.iwfc.service;

import com.iwfc.exception.InvalidBookingException;
import com.iwfc.model.Session;
import com.iwfc.repository.InMemoryRepository;
import com.iwfc.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionServiceTest {

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        Repository<Session, String> repository = new InMemoryRepository<>(Session::getId);
        sessionService = new SessionService(repository);
    }

    @Test
    void bookSession_success() throws InvalidBookingException {
        List<Session> created = sessionService.bookSession("S1", "Morning Yoga", "Studio A", "inst1",
                LocalDateTime.of(2026, 3, 5, 9, 0), 60, false, 10);

        assertEquals(1, created.size());
        assertEquals("S1", created.get(0).getId());
    }

    @Test
    void bookSession_doubleBookingThrowsInvalidBookingException() throws InvalidBookingException {
        sessionService.bookSession("S1", "Morning Yoga", "Studio A", "inst1",
                LocalDateTime.of(2026, 3, 5, 9, 0), 60, false, 10);

        assertThrows(InvalidBookingException.class, () ->
                sessionService.bookSession("S2", "HIIT", "Studio A", "inst1",
                        LocalDateTime.of(2026, 3, 5, 9, 30), 60, false, 10));
    }

    @Test
    void bookSession_outsideOperatingHoursThrowsInvalidBookingException() {
        assertThrows(InvalidBookingException.class, () ->
                sessionService.bookSession("S1", "Early Bird Run", "Studio A", "inst1",
                        LocalDateTime.of(2026, 3, 5, 5, 0), 60, false, 10));
    }

    @Test
    void recurringWeeklySession_createsExpectedOccurrenceCount() throws InvalidBookingException {
        List<Session> created = sessionService.bookSession("S1", "Monday Pilates", "Studio A", "inst1",
                LocalDateTime.of(2026, 3, 2, 9, 0), 60, true, 10);

        assertEquals(4, created.size());
    }

    @Test
    void cancelBooking_freesSlotForRebooking() throws InvalidBookingException {
        sessionService.bookSession("S1", "Spin Class", "EQ-002", "inst1",
                LocalDateTime.of(2026, 3, 5, 9, 0), 60, false, 1);

        sessionService.reserveSpot("S1", "mem1");
        boolean cancelled = sessionService.cancelReservation("S1", "mem1");
        sessionService.reserveSpot("S1", "mem2");

        assertTrue(cancelled);
        assertFalse(sessionService.getSessionOrThrow("S1").isMemberBooked("mem1"));
        assertTrue(sessionService.getSessionOrThrow("S1").isMemberBooked("mem2"));
    }

    /**
     * INTENTIONAL FAIL: this asserts a stricter closing-time boundary (a
     * session must not even END exactly at closing time) than the current
     * SessionService implementation enforces (it only rejects sessions that
     * end AFTER closing time, so ending exactly at 22:00 is currently
     * allowed). Left failing on purpose to satisfy the assignment's
     * "intentional failing test" requirement and to demonstrate that the
     * custom exception mechanism is correctly wired for boundary cases -
     * see README.md for the discussion point this feeds into the report/video.
     */
    @Test
    void bookingExactlyAtClosingTime_shouldThrowInvalidBookingException() {
        assertThrows(InvalidBookingException.class, () ->
                sessionService.bookSession("S1", "Late Session", "Studio A", "inst1",
                        LocalDateTime.of(2026, 3, 5, 21, 0), 60, false, 10));
    }
}
