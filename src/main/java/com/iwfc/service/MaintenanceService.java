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

public class MaintenanceService {

    private final Repository<MaintenanceReport, String> maintenanceRepository;
    private final NotificationCenter notificationCenter;

    public MaintenanceService(Repository<MaintenanceReport, String> maintenanceRepository,
                               NotificationCenter notificationCenter) {
        this.maintenanceRepository = maintenanceRepository;
        this.notificationCenter = notificationCenter;
    }

    public MaintenanceReport reportFault(String id, String equipmentId, String description,
                                          Urgency urgency, String reportedByUsername) {
        MaintenanceReport report = new MaintenanceReport(id, equipmentId, description, urgency, reportedByUsername);
        maintenanceRepository.add(report);
        notificationCenter.notifyAll(String.format(
                "New %s-urgency fault reported on %s: %s", urgency, equipmentId, description));
        return report;
    }

    public void assignTask(String reportId, String assignedToUsername) {
        MaintenanceReport report = getReportOrThrow(reportId);
        report.setAssignedToUsername(assignedToUsername);
        report.setStatus(ReportStatus.ASSIGNED);
        maintenanceRepository.update(report);
        notificationCenter.notifyAll(String.format(
                "Maintenance report %s assigned to %s", reportId, assignedToUsername));
    }

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
        return maintenanceRepository.findAll().stream()
                .collect(Collectors.groupingBy(MaintenanceReport::getUrgency));
    }

    public MaintenanceReport getReportOrThrow(String reportId) {
        return maintenanceRepository.findById(reportId)
                .orElseThrow(() -> new NoSuchElementException("No maintenance report found with ID " + reportId));
    }
}
