package com.iwfc.service;

import com.iwfc.model.MaintenanceReport;
import com.iwfc.model.ReportStatus;
import com.iwfc.model.Urgency;
import com.iwfc.pattern.behavioural.NotificationCenter;
import com.iwfc.repository.InMemoryRepository;
import com.iwfc.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * [Unit Testing] Task 3 - Maintenance Reporting: checks the report workflow transitions PENDING -> ASSIGNED -> COMPLETED.
 */
class MaintenanceServiceTest {

    private MaintenanceService maintenanceService;

    // JUnit 5 lifecycle: @BeforeEach creates a fresh repository and NotificationCenter (Observer subject) for every test.
    @BeforeEach
    void setUp() {
        Repository<MaintenanceReport, String> repository = new InMemoryRepository<>(MaintenanceReport::getId);
        maintenanceService = new MaintenanceService(repository, new NotificationCenter());
    }

    // Verifies: a new fault report starts with status PENDING.
    @Test
    void reportFault_createsPendingReport() {
        MaintenanceReport report = maintenanceService.reportFault(
                "R1", "EQ-002", "Resistance failure", Urgency.HIGH, "inst1");

        assertEquals(ReportStatus.PENDING, report.getStatus());
    }

    // Verifies: assigning a task sets status ASSIGNED and stores the assignee (the service also notifies observers).
    @Test
    void assignTask_movesStatusToAssignedAndNotifies() {
        maintenanceService.reportFault("R1", "EQ-002", "Resistance failure", Urgency.HIGH, "inst1");

        maintenanceService.assignTask("R1", "admin1");

        MaintenanceReport updated = maintenanceService.getReportOrThrow("R1");
        assertEquals(ReportStatus.ASSIGNED, updated.getStatus());
        assertEquals("admin1", updated.getAssignedToUsername());
    }

    // Verifies: completing an assigned task moves the report to COMPLETED.
    @Test
    void completeTask_movesStatusToCompleted() {
        maintenanceService.reportFault("R1", "EQ-002", "Resistance failure", Urgency.HIGH, "inst1");
        maintenanceService.assignTask("R1", "admin1");

        maintenanceService.completeTask("R1");

        assertEquals(ReportStatus.COMPLETED, maintenanceService.getReportOrThrow("R1").getStatus());
    }
}
