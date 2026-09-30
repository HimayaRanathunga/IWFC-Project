package com.iwfc.service;

import com.iwfc.exception.DuplicateEntityException;
import com.iwfc.model.Equipment;
import com.iwfc.model.EquipmentStatus;
import com.iwfc.repository.Repository;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * [Task 1 - Equipment Tracking]
 * Business logic for the equipment inventory: add, edit, deactivate,
 * log usage hours (maintenance-threshold alert) and change status.
 * Single responsibility: only equipment rules, storage is left to the repository.
 */
public class EquipmentService {

    // Dependency inversion: depends on the Repository interface, not a concrete class.
    private final Repository<Equipment, String> equipmentRepository;

    // Constructor injection: the repository is supplied from outside.
    public EquipmentService(Repository<Equipment, String> equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    /** Adds equipment; throws the unchecked DuplicateEntityException if the ID exists. */
    public Equipment addEquipment(String id, String name, String location) {
        if (equipmentRepository.existsById(id)) {
            throw new DuplicateEntityException("Equipment with ID " + id + " already exists");
        }
        Equipment equipment = new Equipment(id, name, location);
        equipmentRepository.add(equipment);
        return equipment;
    }

    public void editEquipment(String id, String newName, String newLocation) {
        Equipment equipment = getEquipmentOrThrow(id);
        equipment.setName(newName);
        equipment.setLocation(newLocation);
        equipmentRepository.update(equipment);
    }

    /** Soft delete: the item is marked inactive, not removed from the repository. */
    public void deactivateEquipment(String id) {
        Equipment equipment = getEquipmentOrThrow(id);
        equipment.deactivate();
        equipmentRepository.update(equipment);
    }

    /** @return true if this usage log pushed the equipment over the maintenance threshold. */
    public boolean logUsageHours(String id, double hours) {
        Equipment equipment = getEquipmentOrThrow(id);
        boolean triggeredAlert = equipment.addUsageHours(hours);
        equipmentRepository.update(equipment);
        return triggeredAlert;
    }

    public void setStatus(String id, EquipmentStatus status) {
        Equipment equipment = getEquipmentOrThrow(id);
        equipment.setStatus(status);
        equipmentRepository.update(equipment);
    }

    public List<Equipment> listAll() {
        return equipmentRepository.findAll();
    }

    public Equipment getEquipmentOrThrow(String id) {
        // Optional + orElseThrow with a lambda (Supplier) creates the exception only when empty.
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No equipment found with ID " + id));
    }
}
