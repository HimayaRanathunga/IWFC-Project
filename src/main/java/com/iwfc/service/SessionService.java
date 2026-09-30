package com.iwfc.service;

import com.iwfc.exception.InvalidBookingException;
import com.iwfc.model.Session;
import com.iwfc.repository.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * [Task 2 - Session Scheduling]
 * Core scheduling logic: creating (booking) a session slot for a resource,
 * and members reserving/cancelling a spot within an existing session.
 * Single responsibility: scheduling rules only; storage is left to the repository.
 */
public class SessionService {

    public static final LocalTime OPENING_TIME = LocalTime.of(6, 0);
    public static final LocalTime CLOSING_TIME = LocalTime.of(22, 0);
    private static final int RECURRING_WEEKLY_OCCURRENCES = 4;

    // Dependency inversion: Repository interface.
    private final Repository<Session, String> sessionRepository;

    // Constructor injection: the repository is supplied from outside.
    public SessionService(Repository<Session, String> sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    /**
     * Books a new session slot for a resource (equipment/studio). Validates
     * operating hours and rejects double-booking of the same resource.
     * When recurring is true, generates weekly follow-on occurrences too,
     * each individually conflict-checked.
     *
     * @return the list of created sessions (size 1 unless recurring)
     */
    public List<Session> bookSession(String idPrefix, String title, String resourceName,
                                      String instructorUsername, LocalDateTime startTime,
                                      int durationMinutes, boolean recurring, int capacity)
            throws InvalidBookingException { // checked exception: callers must handle or declare it

        int occurrences = recurring ? RECURRING_WEEKLY_OCCURRENCES : 1;
        // Generics: a typed list of Session.
        List<Session> created = new java.util.ArrayList<>();

        for (int week = 0; week < occurrences; week++) {
            LocalDateTime occurrenceStart = startTime.plusWeeks(week);
            String sessionId = occurrences == 1 ? idPrefix : idPrefix + "-W" + (week + 1);
            Session candidate = new Session(sessionId, title, resourceName, instructorUsername,
                    occurrenceStart, durationMinutes, recurring, capacity);

            // Checked exceptions from these validations propagate up to the caller.
            validateOperatingHours(candidate);
            validateNoConflict(candidate);

            sessionRepository.add(candidate);
            created.add(candidate);
        }
        return created;
    }

    private void validateOperatingHours(Session session) throws InvalidBookingException {
        LocalTime start = session.getStartTime().toLocalTime();
        LocalTime end = session.getEndTime().toLocalTime();
        boolean sameDay = session.getStartTime().toLocalDate().equals(session.getEndTime().toLocalDate());
        if (!sameDay || start.isBefore(OPENING_TIME) || end.isAfter(CLOSING_TIME)) {
            throw new InvalidBookingException(String.format(
                    "Session '%s' at %s falls outside operating hours (%s - %s)",
                    session.getTitle(), session.getStartTime(), OPENING_TIME, CLOSING_TIME));
        }
    }

    private void validateNoConflict(Session candidate) throws InvalidBookingException {
        // Streams + lambda: anyMatch checks whether any other session overlaps the candidate.
        boolean conflict = sessionRepository.findAll().stream()
                .anyMatch(existing -> !existing.getId().equals(candidate.getId()) && existing.overlaps(candidate));
        if (conflict) {
            throw new InvalidBookingException(String.format(
                    "Resource '%s' is already booked during %s - %s",
                    candidate.getResourceName(), candidate.getStartTime(), candidate.getEndTime()));
        }
    }

    /** Reserves a spot: rejects a duplicate booking or a full session (checked exception). */
    public void reserveSpot(String sessionId, String memberUsername) throws InvalidBookingException {
        Session session = getSessionOrThrow(sessionId);
        if (session.isMemberBooked(memberUsername)) {
            throw new InvalidBookingException(memberUsername + " has already booked session " + sessionId);
        }
        if (session.isFull()) {
            throw new InvalidBookingException("Session " + sessionId + " is fully booked");
        }
        session.addBooking(memberUsername);
        sessionRepository.update(session);
    }

    public boolean cancelReservation(String sessionId, String memberUsername) {
        Session session = getSessionOrThrow(sessionId);
        boolean removed = session.removeBooking(memberUsername);
        if (removed) {
            sessionRepository.update(session);
        }
        return removed;
    }

    public List<Session> listAll() {
        return sessionRepository.findAll();
    }

    public List<Session> listByInstructor(String instructorUsername) {
        // Streams: filter with a lambda, then collect to a list.
        return sessionRepository.findAll().stream()
                .filter(s -> s.getInstructorUsername().equals(instructorUsername))
                .collect(Collectors.toList());
    }

    public Session getSessionOrThrow(String sessionId) {
        // Optional.orElseThrow with a lambda supplier.
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("No session found with ID " + sessionId));
    }
}
