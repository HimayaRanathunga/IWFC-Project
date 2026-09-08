package com.iwfc.model;

import java.util.Objects;

public class MaintenanceReport {

    private final String id;
    private final String equipmentId;
    private final String description;
    private final Urgency urgency;
    private final String reportedByUsername;
    private ReportStatus status;
    private String assignedToUsername;

    public MaintenanceReport(String id, String equipmentId, String description,
                              Urgency urgency, String reportedByUsername) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.description = description;
        this.urgency = urgency;
        this.reportedByUsername = reportedByUsername;
        this.status = ReportStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getDescription() {
        return description;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public String getReportedByUsername() {
        return reportedByUsername;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getAssignedToUsername() {
        return assignedToUsername;
    }

    public void setAssignedToUsername(String assignedToUsername) {
        this.assignedToUsername = assignedToUsername;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaintenanceReport)) return false;
        MaintenanceReport that = (MaintenanceReport) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("MaintenanceReport[%s] equipment=%s urgency=%s status=%s reportedBy=%s assignedTo=%s - %s",
                id, equipmentId, urgency, status, reportedByUsername,
                assignedToUsername == null ? "-" : assignedToUsername, description);
    }
}
