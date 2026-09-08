package com.iwfc.model;

import java.util.Objects;

/**
 * Encapsulation: usageHours has no public setter. The only way to change it
 * is addUsageHours(), which enforces the maintenance-alert invariant itself
 * rather than trusting callers to remember to check it.
 */
public class Equipment {

    public static final double MAINTENANCE_THRESHOLD_HOURS = 100.0;

    private final String id;
    private String name;
    private EquipmentStatus status;
    private String location;
    private double usageHours;
    private boolean active = true;

    public Equipment(String id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.status = EquipmentStatus.OPERATIONAL;
        this.usageHours = 0.0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getUsageHours() {
        return usageHours;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    /**
     * @return true if this call pushed the equipment over the maintenance
     * threshold and its status was automatically flipped to UNDER_MAINTENANCE.
     */
    public boolean addUsageHours(double hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("Usage hours cannot be negative");
        }
        boolean wasBelowThreshold = usageHours < MAINTENANCE_THRESHOLD_HOURS;
        usageHours += hours;
        boolean crossedThreshold = wasBelowThreshold && usageHours >= MAINTENANCE_THRESHOLD_HOURS;
        if (crossedThreshold && status == EquipmentStatus.OPERATIONAL) {
            status = EquipmentStatus.UNDER_MAINTENANCE;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipment)) return false;
        Equipment equipment = (Equipment) o;
        return id.equals(equipment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Equipment[%s] %s @ %s - %s (%.1fh)%s",
                id, name, location, status, usageHours, active ? "" : " [INACTIVE]");
    }
}
