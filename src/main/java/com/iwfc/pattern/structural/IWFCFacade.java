package com.iwfc.pattern.structural;

import com.iwfc.exception.InvalidBookingException;
import com.iwfc.exception.UnauthorizedAccessException;
import com.iwfc.model.Equipment;
import com.iwfc.model.EquipmentStatus;
import com.iwfc.model.MaintenanceReport;
import com.iwfc.model.Role;
import com.iwfc.model.Session;
import com.iwfc.model.Urgency;
import com.iwfc.model.User;
import com.iwfc.pattern.creational.SystemManager;
import com.iwfc.service.EquipmentService;
import com.iwfc.service.MaintenanceService;
import com.iwfc.service.SessionService;
import com.iwfc.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Facade pattern: the console UI talks to this single class instead of
 * coordinating four separate services plus the SystemManager singleton
 * directly. Also the single place admin-only checks are enforced, so they
 * hold regardless of how menus are wired.
 */
public class IWFCFacade {

    private final EquipmentService equipmentService;
    private final SessionService sessionService;
    private final MaintenanceService maintenanceService;
    private final UserService userService;

    public IWFCFacade(SystemManager systemManager) {
        this.equipmentService = new EquipmentService(systemManager.getEquipmentRepository());
        this.sessionService = new SessionService(systemManager.getSessionRepository());
        this.maintenanceService = new MaintenanceService(
                systemManager.getMaintenanceRepository(), systemManager.getNotificationCenter());
        this.userService = new UserService(systemManager.getUserRepository(), systemManager.getNotificationCenter());
    }

    // --- Equipment ---

    public Equipment registerEquipment(String id, String name, String location) {
        return equipmentService.addEquipment(id, name, location);
    }

    public void editEquipment(String id, String name, String location) {
        equipmentService.editEquipment(id, name, location);
    }

    public void deactivateEquipment(String id) {
        equipmentService.deactivateEquipment(id);
    }

    public boolean logEquipmentUsage(String id, double hours) {
        return equipmentService.logUsageHours(id, hours);
    }

    public void setEquipmentStatus(String id, EquipmentStatus status) {
        equipmentService.setStatus(id, status);
    }

    public List<Equipment> listEquipment() {
        return equipmentService.listAll();
    }

    // --- Users ---

    public void registerUser(User user) {
        userService.registerUser(user);
    }

    public Optional<User> findUser(String username) {
        return userService.findByUsername(username);
    }

    public List<User> listUsersByRole(Role role) {
        return userService.findByRole(role);
    }

    // --- Sessions ---

    public List<Session> bookSession(String idPrefix, String title, String resourceName,
                                      String instructorUsername, LocalDateTime startTime,
                                      int durationMinutes, boolean recurring, int capacity)
            throws InvalidBookingException {
        return sessionService.bookSession(idPrefix, title, resourceName, instructorUsername,
                startTime, durationMinutes, recurring, capacity);
    }

    public void reserveSpot(String sessionId, String memberUsername) throws InvalidBookingException {
        sessionService.reserveSpot(sessionId, memberUsername);
    }

    public boolean cancelReservation(String sessionId, String memberUsername) {
        return sessionService.cancelReservation(sessionId, memberUsername);
    }

    public List<Session> listSessions() {
        return sessionService.listAll();
    }

    public List<Session> listSessionsByInstructor(String instructorUsername) {
        return sessionService.listByInstructor(instructorUsername);
    }

    // --- Maintenance ---

    public MaintenanceReport reportFault(String id, String equipmentId, String description,
                                          Urgency urgency, String reportedByUsername) {
        return maintenanceService.reportFault(id, equipmentId, description, urgency, reportedByUsername);
    }

    public void assignTask(User caller, String reportId, String assignedToUsername) {
        requireAdministrator(caller);
        maintenanceService.assignTask(reportId, assignedToUsername);
    }

    public void completeTask(User caller, String reportId) {
        requireAdministrator(caller);
        maintenanceService.completeTask(reportId);
    }

    public List<MaintenanceReport> viewGlobalMaintenanceLog(User caller) {
        requireAdministrator(caller);
        return maintenanceService.listAll();
    }

    public Map<Urgency, List<MaintenanceReport>> maintenanceLogByUrgency(User caller) {
        requireAdministrator(caller);
        return maintenanceService.groupByUrgency();
    }

    private void requireAdministrator(User caller) {
        if (caller.getRole() != Role.ADMINISTRATOR) {
            throw new UnauthorizedAccessException(
                    caller.getUsername() + " (" + caller.getRole() + ") is not authorized to perform this action");
        }
    }
}
