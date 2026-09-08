package com.iwfc.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Session {

    private final String id;
    private final String title;
    private final String resourceName;
    private final String instructorUsername;
    private final LocalDateTime startTime;
    private final int durationMinutes;
    private final boolean recurring;
    private final int capacity;
    private final List<String> bookedMemberUsernames = new ArrayList<>();

    public Session(String id, String title, String resourceName, String instructorUsername,
                   LocalDateTime startTime, int durationMinutes, boolean recurring, int capacity) {
        this.id = id;
        this.title = title;
        this.resourceName = resourceName;
        this.instructorUsername = instructorUsername;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.recurring = recurring;
        this.capacity = capacity;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getInstructorUsername() {
        return instructorUsername;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(durationMinutes);
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<String> getBookedMemberUsernames() {
        return List.copyOf(bookedMemberUsernames);
    }

    public boolean isFull() {
        return bookedMemberUsernames.size() >= capacity;
    }

    public boolean isMemberBooked(String memberUsername) {
        return bookedMemberUsernames.contains(memberUsername);
    }

    public void addBooking(String memberUsername) {
        bookedMemberUsernames.add(memberUsername);
    }

    public boolean removeBooking(String memberUsername) {
        return bookedMemberUsernames.remove(memberUsername);
    }

    /** True if this session shares the same resource and its time range overlaps the other's. */
    public boolean overlaps(Session other) {
        if (!resourceName.equalsIgnoreCase(other.resourceName)) {
            return false;
        }
        return startTime.isBefore(other.getEndTime()) && other.getStartTime().isBefore(getEndTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Session)) return false;
        Session session = (Session) o;
        return id.equals(session.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Session[%s] %s @ %s with %s on %s for %dmin (%d/%d booked)%s",
                id, title, resourceName, instructorUsername, startTime, durationMinutes,
                bookedMemberUsernames.size(), capacity, recurring ? " [recurring]" : "");
    }
}
