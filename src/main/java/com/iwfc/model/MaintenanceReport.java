package com.iwfc.model;

import java.util.Objects;

/**
 * [Task 3 - Maintenance Reporting] A fault report for a piece of equipment.
 *
 * Encapsulation: the facts of the report (id, equipment, description, urgency, reporter)
 * are final. Only the workflow fields (status and assignee) can change later.
 */
public class MaintenanceReport {

    // Immutable identity: set once in the constructor.
    private final String id;
    private final String equipmentId;
    private final String description;
    private final Urgency urgency;
    private final String reportedByUsername;
    // Mutable workflow state: enum-based status (PENDING, ASSIGNED, COMPLETED) and the assignee.
    private ReportStatus status;
    private String assignedToUsername;

    public MaintenanceReport(String id, String equipmentId, String description,
                              Urgency urgency, String reportedByUsername) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.description = description;
        this.urgency = urgency;
        this.reportedByUsername = reportedByUsername;
        // Every new report starts as PENDING.
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

    // equals/hashCode contract: identity is the id only, and hashCode uses the same field.
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
