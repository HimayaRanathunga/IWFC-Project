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
 * [Creational Pattern - Singleton]
 * Singleton pattern: exactly one in-memory data store must exist for the
 * whole application so every service and console menu observes the same
 * equipment/session/report/user state without repositories being threaded
 * through every constructor.
 * Implementation: lazy initialisation with a synchronized getInstance()
 * (simple thread safety, not double-checked locking).
 * Also acts as the composition root: it owns the repositories and the NotificationCenter.
 */
public final class SystemManager {

    // Singleton: the one shared instance, created on first use (lazy).
    private static SystemManager instance;

    // Composition: SystemManager owns these parts. Dependency inversion: typed as the
    // Repository interface, not the concrete InMemoryRepository. Generics: Repository<T, ID>.
    private final Repository<Equipment, String> equipmentRepository;
    private final Repository<Session, String> sessionRepository;
    private final Repository<MaintenanceReport, String> maintenanceRepository;
    private final Repository<User, String> userRepository;
    private final NotificationCenter notificationCenter;

    // Singleton: private constructor so no other class can create a second instance.
    private SystemManager() {
        // Method references (Equipment::getId) tell each repository how to read an entity's id.
        this.equipmentRepository = new InMemoryRepository<>(Equipment::getId);
        this.sessionRepository = new InMemoryRepository<>(Session::getId);
        this.maintenanceRepository = new InMemoryRepository<>(MaintenanceReport::getId);
        this.userRepository = new InMemoryRepository<>(User::getUsername);
        this.notificationCenter = new NotificationCenter();
        seedDemoData();
    }

    /** Singleton: global access point. synchronized makes lazy creation thread-safe. */
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

    /** Loads demo equipment and users so the console app has data on first run. */
    private void seedDemoData() {
        equipmentRepository.add(new Equipment("EQ-001", "Treadmill", "Cardio Zone"));
        equipmentRepository.add(new Equipment("EQ-002", "Spin Bike", "Studio A"));
        equipmentRepository.add(new Equipment("EQ-003", "Rowing Machine", "Cardio Zone"));

        // Polymorphism: subclasses stored through the User supertype.
        User admin = new Administrator("admin1", "Alice Admin");
        User instructor = new Instructor("inst1", "Ian Instructor");
        User member1 = new Member("mem1", "Mary Member");
        User member2 = new Member("mem2", "Max Member");

        for (User user : new User[]{admin, instructor, member1, member2}) {
            userRepository.add(user);
            // Observer: every User is an observer, so all users get system notifications.
            notificationCenter.registerObserver(user);
        }
    }
}
