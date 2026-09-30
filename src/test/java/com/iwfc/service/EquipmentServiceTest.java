package com.iwfc.service;

import com.iwfc.exception.DuplicateEntityException;
import com.iwfc.model.Equipment;
import com.iwfc.model.EquipmentStatus;
import com.iwfc.repository.InMemoryRepository;
import com.iwfc.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * [Unit Testing] Task 1 - Equipment Tracking: adding equipment, duplicate data and the maintenance threshold alert.
 */
class EquipmentServiceTest {

    private EquipmentService equipmentService;

    // JUnit 5 lifecycle: @BeforeEach builds a new service with an empty repository before each test (tests stay independent).
    @BeforeEach
    void setUp() {
        Repository<Equipment, String> repository = new InMemoryRepository<>(Equipment::getId);
        equipmentService = new EquipmentService(repository);
    }

    // Verifies: new equipment is stored and starts as OPERATIONAL.
    @Test
    void addEquipment_success() {
        Equipment equipment = equipmentService.addEquipment("EQ-1", "Treadmill", "Cardio Zone");

        assertEquals("EQ-1", equipment.getId());
        assertEquals(EquipmentStatus.OPERATIONAL, equipment.getStatus());
    }

    // Verifies: duplicate data handling - adding the same ID twice throws DuplicateEntityException.
    @Test
    void addEquipment_duplicateIdThrowsException() {
        equipmentService.addEquipment("EQ-1", "Treadmill", "Cardio Zone");

        // assertThrows with a lambda: the code inside must throw the expected exception.
        assertThrows(DuplicateEntityException.class,
                () -> equipmentService.addEquipment("EQ-1", "Another Treadmill", "Studio A"));
    }

    // Verifies: logging usage hours reaches the threshold, returns an alert and moves the status to UNDER_MAINTENANCE.
    @Test
    void addUsageHours_triggersMaintenanceAlertAtThreshold() {
        equipmentService.addEquipment("EQ-1", "Treadmill", "Cardio Zone");

        boolean alertBelowThreshold = equipmentService.logUsageHours("EQ-1", 50.0);
        boolean alertAtThreshold = equipmentService.logUsageHours("EQ-1", 60.0);

        assertTrue(!alertBelowThreshold && alertAtThreshold);
        assertEquals(EquipmentStatus.UNDER_MAINTENANCE,
                equipmentService.getEquipmentOrThrow("EQ-1").getStatus());
    }
}
