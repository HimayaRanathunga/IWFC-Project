package com.iwfc.service;

import com.iwfc.model.MaintenanceReport;
import com.iwfc.model.ReportStatus;
import com.iwfc.model.Urgency;
import com.iwfc.pattern.behavioural.NotificationCenter;
import com.iwfc.repository.Repository;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * [Task 3 - Maintenance Reporting]
 * Business logic for fault reports: report a fault, assign a task, complete a task
 * and group reports by urgency. Acts as an Observer-pattern publisher: it calls the
 * NotificationCenter after each change.
 * Note: status changes are not validated (any transition is accepted).
 */
public class MaintenanceService {

    // Dependency inversion: Repository interface. Composition: the service uses the shared NotificationCenter.
    private final Repository<MaintenanceReport, String> maintenanceRepository;
    private final NotificationCenter notificationCenter;

    // Constructor injection of both collaborators.
    public MaintenanceService(Repository<MaintenanceReport, String> maintenanceRepository,
                               NotificationCenter notificationCenter) {
        this.maintenanceRepository = maintenanceRepository;
        this.notificationCenter = notificationCenter;
    }

    public MaintenanceReport reportFault(String id, String equipmentId, String description,
                                          Urgency urgency, String reportedByUsername) {
        MaintenanceReport report = new MaintenanceReport(id, equipmentId, description, urgency, reportedByUsername);
        maintenanceRepository.add(report);
        // Observer: publisher role, tells all subscribers a new fault was reported.
        notificationCenter.notifyAll(String.format(
                "New %s-urgency fault reported on %s: %s", urgency, equipmentId, description));
        return report;
    }

    /** Sets the assignee and status ASSIGNED, then notifies observers. Current status is not checked. */
    public void assignTask(String reportId, String assignedToUsername) {
        MaintenanceReport report = getReportOrThrow(reportId);
        report.setAssignedToUsername(assignedToUsername);
        report.setStatus(ReportStatus.ASSIGNED);
        maintenanceRepository.update(report);
        notificationCenter.notifyAll(String.format(
                "Maintenance report %s assigned to %s", reportId, assignedToUsername));
    }

    /** Sets status COMPLETED and notifies observers. Current status is not checked. */
    public void completeTask(String reportId) {
        MaintenanceReport report = getReportOrThrow(reportId);
        report.setStatus(ReportStatus.COMPLETED);
        maintenanceRepository.update(report);
        notificationCenter.notifyAll(String.format("Maintenance report %s marked as completed", reportId));
    }

    public List<MaintenanceReport> listAll() {
        return maintenanceRepository.findAll();
    }

    public Map<Urgency, List<MaintenanceReport>> groupByUrgency() {
        // Streams: groupingBy with a method reference builds a Map of urgency -> reports.
        return maintenanceRepository.findAll().stream()
                .collect(Collectors.groupingBy(MaintenanceReport::getUrgency));
    }

    public MaintenanceReport getReportOrThrow(String reportId) {
        // Optional.orElseThrow with a lambda supplier.
        return maintenanceRepository.findById(reportId)
                .orElseThrow(() -> new NoSuchElementException("No maintenance report found with ID " + reportId));
    }
}
