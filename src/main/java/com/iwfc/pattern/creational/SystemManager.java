package com.iwfc.pattern.creational;

import com.iwfc.model.Administrator;
import com.iwfc.model.Equipment;
import com.iwfc.model.Instructor;
import com.iwfc.model.MaintenanceReport;
import com.iwfc.model.Member;
import com.iwfc.model.Session;
import com.iwfc.model.User;
import com.iwfc.pattern.behavioural.NotificationCenter;
import com.iwfc.repository.InMemoryRepository;
import com.iwfc.repository.Repository;

/**
 * Singleton pattern: exactly one in-memory data store must exist for the
 * whole application so every service and console menu observes the same
 * equipment/session/report/user state without repositories being threaded
 * through every constructor.
 */
public final class SystemManager {

    private static SystemManager instance;

    private final Repository<Equipment, String> equipmentRepository;
    private final Repository<Session, String> sessionRepository;
    private final Repository<MaintenanceReport, String> maintenanceRepository;
    private final Repository<User, String> userRepository;
    private final NotificationCenter notificationCenter;

    private SystemManager() {
        this.equipmentRepository = new InMemoryRepository<>(Equipment::getId);
        this.sessionRepository = new InMemoryRepository<>(Session::getId);
        this.maintenanceRepository = new InMemoryRepository<>(MaintenanceReport::getId);
        this.userRepository = new InMemoryRepository<>(User::getUsername);
        this.notificationCenter = new NotificationCenter();
        seedDemoData();
    }

    public static synchronized SystemManager getInstance() {
        if (instance == null) {
            instance = new SystemManager();
        }
        return instance;
    }

    public Repository<Equipment, String> getEquipmentRepository() {
        return equipmentRepository;
    }

    public Repository<Session, String> getSessionRepository() {
        return sessionRepository;
    }

    public Repository<MaintenanceReport, String> getMaintenanceRepository() {
        return maintenanceRepository;
    }

    public Repository<User, String> getUserRepository() {
        return userRepository;
    }

    public NotificationCenter getNotificationCenter() {
        return notificationCenter;
    }

    private void seedDemoData() {
        equipmentRepository.add(new Equipment("EQ-001", "Treadmill", "Cardio Zone"));
        equipmentRepository.add(new Equipment("EQ-002", "Spin Bike", "Studio A"));
        equipmentRepository.add(new Equipment("EQ-003", "Rowing Machine", "Cardio Zone"));

        User admin = new Administrator("admin1", "Alice Admin");
        User instructor = new Instructor("inst1", "Ian Instructor");
        User member1 = new Member("mem1", "Mary Member");
        User member2 = new Member("mem2", "Max Member");

        for (User user : new User[]{admin, instructor, member1, member2}) {
            userRepository.add(user);
            notificationCenter.registerObserver(user);
        }
    }
}
