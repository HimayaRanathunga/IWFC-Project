package com.iwfc.ui;

import com.iwfc.exception.DuplicateEntityException;
import com.iwfc.exception.InvalidBookingException;
import com.iwfc.exception.UnauthorizedAccessException;
import com.iwfc.model.Instructor;
import com.iwfc.model.Member;
import com.iwfc.model.Role;
import com.iwfc.model.Session;
import com.iwfc.model.Urgency;
import com.iwfc.model.User;
import com.iwfc.pattern.creational.SystemManager;
import com.iwfc.pattern.structural.IWFCFacade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Console entry point. Talks only to {@link IWFCFacade} - never to the
 * services or repositories directly - and is responsible for catching the
 * custom exceptions and turning them into friendly console messages.
 */
public class ConsoleApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleUtil util = new ConsoleUtil(scanner);
        IWFCFacade facade = new IWFCFacade(SystemManager.getInstance());

        System.out.println("==============================================");
        System.out.println(" Intelligent Wellness and Fitness Center (IWFC)");
        System.out.println("==============================================");

        boolean running = true;
        while (running) {
            User currentUser = login(util, facade);
            if (currentUser == null) {
                running = false;
                continue;
            }
            switch (currentUser.getRole()) {
                case ADMINISTRATOR -> adminMenu(util, facade, currentUser);
                case INSTRUCTOR -> instructorMenu(util, facade, currentUser);
                case MEMBER -> memberMenu(util, facade, currentUser);
            }
        }
        System.out.println("Goodbye!");
    }

    private static User login(ConsoleUtil util, IWFCFacade facade) {
        System.out.println("\n--- Login ---");
        System.out.println("1. Administrator");
        System.out.println("2. Instructor");
        System.out.println("3. Member");
        System.out.println("0. Exit");
        int choice = util.readInt("Select role: ");

        Role role;
        switch (choice) {
            case 1 -> role = Role.ADMINISTRATOR;
            case 2 -> role = Role.INSTRUCTOR;
            case 3 -> role = Role.MEMBER;
            case 0 -> {
                return null;
            }
            default -> {
                System.out.println("Invalid choice.");
                return login(util, facade);
            }
        }

        List<User> candidates = facade.listUsersByRole(role);
        if (candidates.isEmpty()) {
            System.out.println("No users registered for that role yet.");
            return login(util, facade);
        }
        System.out.println("Available usernames: ");
        candidates.forEach(u -> System.out.println("  - " + u.getUsername() + " (" + u.getName() + ")"));
        String username = util.readNonEmptyString("Enter username: ");
        return facade.findUser(username)
                .filter(u -> u.getRole() == role)
                .orElseGet(() -> {
                    System.out.println("Username not found for that role.");
                    return login(util, facade);
                });
    }

    // --- Administrator ---

    private static void adminMenu(ConsoleUtil util, IWFCFacade facade, User admin) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- Administrator Menu (" + admin.getUsername() + ") ---");
            System.out.println("1. View all equipment");
            System.out.println("2. Add equipment");
            System.out.println("3. Edit equipment");
            System.out.println("4. Deactivate equipment");
            System.out.println("5. View global maintenance log");
            System.out.println("6. Assign maintenance task");
            System.out.println("7. Complete maintenance task");
            System.out.println("8. Register instructor/member");
            System.out.println("0. Logout");
            int choice = util.readInt("Select option: ");
            try {
                switch (choice) {
                    case 1 -> facade.listEquipment().forEach(System.out::println);
                    case 2 -> {
                        String id = util.readNonEmptyString("Equipment ID: ");
                        String name = util.readNonEmptyString("Name: ");
                        String location = util.readNonEmptyString("Location: ");
                        System.out.println("Added: " + facade.registerEquipment(id, name, location));
                    }
                    case 3 -> {
                        String id = util.readNonEmptyString("Equipment ID to edit: ");
                        String name = util.readNonEmptyString("New name: ");
                        String location = util.readNonEmptyString("New location: ");
                        facade.editEquipment(id, name, location);
                        System.out.println("Updated.");
                    }
                    case 4 -> {
                        String id = util.readNonEmptyString("Equipment ID to deactivate: ");
                        facade.deactivateEquipment(id);
                        System.out.println("Deactivated.");
                    }
                    case 5 -> facade.viewGlobalMaintenanceLog(admin).forEach(System.out::println);
                    case 6 -> {
                        String reportId = util.readNonEmptyString("Report ID: ");
                        String assignee = util.readNonEmptyString("Assign to (instructor username): ");
                        facade.assignTask(admin, reportId, assignee);
                        System.out.println("Assigned.");
                    }
                    case 7 -> {
                        String reportId = util.readNonEmptyString("Report ID: ");
                        facade.completeTask(admin, reportId);
                        System.out.println("Marked complete.");
                    }
                    case 8 -> registerUserFlow(util, facade);
                    case 0 -> inMenu = false;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (DuplicateEntityException | UnauthorizedAccessException | NoSuchElementException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void registerUserFlow(ConsoleUtil util, IWFCFacade facade) {
        System.out.println("1. Instructor  2. Member");
        int roleChoice = util.readInt("Role: ");
        String username = util.readNonEmptyString("Username: ");
        String name = util.readNonEmptyString("Full name: ");
        try {
            User user = roleChoice == 1 ? new Instructor(username, name) : new Member(username, name);
            facade.registerUser(user);
            System.out.println("Registered: " + user);
        } catch (DuplicateEntityException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // --- Instructor ---

    private static void instructorMenu(ConsoleUtil util, IWFCFacade facade, User instructor) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- Instructor Menu (" + instructor.getUsername() + ") ---");
            System.out.println("1. View my sessions");
            System.out.println("2. Create session");
            System.out.println("3. Report equipment fault");
            System.out.println("4. Log equipment usage hours");
            System.out.println("0. Logout");
            int choice = util.readInt("Select option: ");
            try {
                switch (choice) {
                    case 1 -> facade.listSessionsByInstructor(instructor.getUsername()).forEach(System.out::println);
                    case 2 -> createSessionFlow(util, facade, instructor);
                    case 3 -> {
                        String reportId = util.readNonEmptyString("Report ID: ");
                        String equipmentId = util.readNonEmptyString("Equipment ID: ");
                        String description = util.readNonEmptyString("Description: ");
                        Urgency urgency = readUrgency(util);
                        facade.reportFault(reportId, equipmentId, description, urgency, instructor.getUsername());
                        System.out.println("Fault reported.");
                    }
                    case 4 -> {
                        String equipmentId = util.readNonEmptyString("Equipment ID: ");
                        double hours = util.readDouble("Hours used: ");
                        boolean alert = facade.logEquipmentUsage(equipmentId, hours);
                        System.out.println("Logged." + (alert ? " ALERT: equipment now flagged for maintenance!" : ""));
                    }
                    case 0 -> inMenu = false;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (NoSuchElementException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void createSessionFlow(ConsoleUtil util, IWFCFacade facade, User instructor) {
        String id = util.readNonEmptyString("Session ID: ");
        String title = util.readNonEmptyString("Title: ");
        String resource = util.readNonEmptyString("Resource (equipment ID or studio name): ");
        LocalDateTime start = util.readDateTime("Start time");
        int duration = util.readInt("Duration (minutes): ");
        boolean recurring = util.readYesNo("Recurring weekly?");
        int capacity = util.readInt("Capacity: ");
        try {
            List<Session> created = facade.bookSession(id, title, resource, instructor.getUsername(),
                    start, duration, recurring, capacity);
            created.forEach(System.out::println);
        } catch (InvalidBookingException e) {
            System.out.println("Booking rejected: " + e.getMessage());
        }
    }

    private static Urgency readUrgency(ConsoleUtil util) {
        System.out.println("1. Low  2. Medium  3. High");
        int choice = util.readInt("Urgency: ");
        return switch (choice) {
            case 1 -> Urgency.LOW;
            case 3 -> Urgency.HIGH;
            default -> Urgency.MEDIUM;
        };
    }

    // --- Member ---

    private static void memberMenu(ConsoleUtil util, IWFCFacade facade, User member) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n--- Member Menu (" + member.getUsername() + ") ---");
            System.out.println("1. View available schedule");
            System.out.println("2. Book a session");
            System.out.println("3. Cancel my booking");
            System.out.println("4. View my notifications");
            System.out.println("0. Logout");
            int choice = util.readInt("Select option: ");
            try {
                switch (choice) {
                    case 1 -> facade.listSessions().forEach(System.out::println);
                    case 2 -> {
                        String sessionId = util.readNonEmptyString("Session ID to book: ");
                        facade.reserveSpot(sessionId, member.getUsername());
                        System.out.println("Booked.");
                    }
                    case 3 -> {
                        String sessionId = util.readNonEmptyString("Session ID to cancel: ");
                        boolean cancelled = facade.cancelReservation(sessionId, member.getUsername());
                        System.out.println(cancelled ? "Cancelled." : "You had no booking on that session.");
                    }
                    case 4 -> {
                        if (member instanceof Member m) {
                            m.getNotifications().forEach(System.out::println);
                        }
                    }
                    case 0 -> inMenu = false;
                    default -> System.out.println("Invalid choice.");
                }
            } catch (NoSuchElementException | InvalidBookingException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
