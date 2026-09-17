package com.iwfc.ui;

import com.iwfc.exception.DuplicateEntityException;
import com.iwfc.exception.InvalidBookingException;
import com.iwfc.exception.UnauthorizedAccessException;
import com.iwfc.model.Equipment;
import com.iwfc.model.Instructor;
import com.iwfc.model.MaintenanceReport;
import com.iwfc.model.Member;
import com.iwfc.model.Role;
import com.iwfc.model.Session;
import com.iwfc.model.Urgency;
import com.iwfc.model.User;
import com.iwfc.pattern.creational.SystemManager;
import com.iwfc.pattern.structural.IWFCFacade;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static com.iwfc.ui.ConsoleUtil.*;

/**
 * Modern, styled Console Entry Point for IWFC.
 * Employs ANSI color accents, Unicode box-drawing tables, and clean role-based navigation.
 */
public class ConsoleApp {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ConsoleUtil util = new ConsoleUtil(scanner);
        IWFCFacade facade = new IWFCFacade(SystemManager.getInstance());

        printBanner();

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

        System.out.println("\n" + CYAN + "================================================================" + RESET);
        System.out.println(GREEN + BOLD + "   Thank you for using IWFC Enterprise Management System! Goodbye. " + RESET);
        System.out.println(CYAN + "================================================================" + RESET + "\n");
    }

    private static void printBanner() {
        System.out.println(CYAN + "╔══════════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║" + BOLD + WHITE + "   🏋️  INTELLIGENT WELLNESS & FITNESS CENTER (IWFC) SYSTEM      " + RESET + CYAN + "║" + RESET);
        System.out.println(CYAN + "║" + DIM + "   Advanced Object-Oriented Management System • CMP 7001 MSc    " + RESET + CYAN + "║" + RESET);
        System.out.println(CYAN + "╚══════════════════════════════════════════════════════════════════╝" + RESET);
    }

    private static User login(ConsoleUtil util, IWFCFacade facade) {
        System.out.println("\n" + BOLD + "┌────────────────────────────────────────┐" + RESET);
        System.out.println(BOLD + "│        🔐 SELECT SYSTEM ACCESS ROLE    │" + RESET);
        System.out.println(BOLD + "├────────────────────────────────────────┤" + RESET);
        System.out.println("│  " + MAGENTA + "1." + RESET + " 👑 Administrator                   │");
        System.out.println("│  " + CYAN + "2." + RESET + " 🏋️  Instructor (Staff)               │");
        System.out.println("│  " + GREEN + "3." + RESET + " 👤 Member (Client)                  │");
        System.out.println("│  " + RED + "0." + RESET + " 🚪 Exit Application                 │");
        System.out.println(BOLD + "└────────────────────────────────────────┘" + RESET);

        int choice = util.readInt("❯ Choose access role: ");

        Role role;
        switch (choice) {
            case 1 -> role = Role.ADMINISTRATOR;
            case 2 -> role = Role.INSTRUCTOR;
            case 3 -> role = Role.MEMBER;
            case 0 -> {
                return null;
            }
            default -> {
                ConsoleUtil.printError("Invalid choice! Please select 0, 1, 2, or 3.");
                return login(util, facade);
            }
        }

        List<User> candidates = facade.listUsersByRole(role);
        if (candidates.isEmpty()) {
            ConsoleUtil.printWarning("No users registered for role: " + role);
            return login(util, facade);
        }

        System.out.println("\n" + DIM + "Available registered accounts:" + RESET);
        candidates.forEach(u -> System.out.println("   • " + BOLD + u.getUsername() + RESET + " (" + u.getName() + ")"));

        String username = util.readNonEmptyString("❯ Enter username: ");
        return facade.findUser(username)
                .filter(u -> u.getRole() == role)
                .orElseGet(() -> {
                    ConsoleUtil.printError("Username not found for role " + role + ". Please try again.");
                    return login(util, facade);
                });
    }

    // =========================================================================
    // 👑 ADMINISTRATOR WORKFLOW
    // =========================================================================

    private static void adminMenu(ConsoleUtil util, IWFCFacade facade, User admin) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n" + MAGENTA + BOLD + "╔═══════════════════════════════════════════════════════════╗" + RESET);
            System.out.println(MAGENTA + BOLD + "║  👑 ADMINISTRATOR PORTAL : " + String.format("%-31s", admin.getName()) + "║" + RESET);
            System.out.println(MAGENTA + BOLD + "╠═══════════════════════════════════════════════════════════╣" + RESET);
            System.out.println("║  " + CYAN + "1." + RESET + " 📋 View Equipment Inventory (Tabular)                 ║");
            System.out.println("║  " + CYAN + "2." + RESET + " ➕ Register New Equipment                              ║");
            System.out.println("║  " + CYAN + "3." + RESET + " ✏️  Edit Equipment Details                              ║");
            System.out.println("║  " + CYAN + "4." + RESET + " 🚫 Deactivate Equipment                               ║");
            System.out.println("║  " + CYAN + "5." + RESET + " 🛠️  View Global Maintenance Tickets                   ║");
            System.out.println("║  " + CYAN + "6." + RESET + " 👷 Assign Maintenance Ticket                          ║");
            System.out.println("║  " + CYAN + "7." + RESET + " ✅ Complete Maintenance Ticket                        ║");
            System.out.println("║  " + CYAN + "8." + RESET + " 👥 Register Instructor / Member Account                ║");
            System.out.println("║  " + RED + "0." + RESET + " 🔙 Logout to Main Login Screen                        ║");
            System.out.println(MAGENTA + BOLD + "╚═══════════════════════════════════════════════════════════╝" + RESET);

            int choice = util.readInt("❯ Select administrative action: ");
            try {
                switch (choice) {
                    case 1 -> renderEquipmentTable(facade.listEquipment());
                    case 2 -> {
                        String id = util.readNonEmptyString("❯ Equipment ID (e.g. EQ-004): ");
                        String name = util.readNonEmptyString("❯ Commercial Model Name: ");
                        String location = util.readNonEmptyString("❯ Facility Location: ");
                        Equipment created = facade.registerEquipment(id, name, location);
                        ConsoleUtil.printSuccess("Equipment successfully registered: " + created.getName() + " [" + created.getId() + "]");
                    }
                    case 3 -> {
                        String id = util.readNonEmptyString("❯ Equipment ID to update: ");
                        String name = util.readNonEmptyString("❯ Updated Model Name: ");
                        String location = util.readNonEmptyString("❯ Updated Location: ");
                        facade.editEquipment(id, name, location);
                        ConsoleUtil.printSuccess("Equipment record updated successfully.");
                    }
                    case 4 -> {
                        String id = util.readNonEmptyString("❯ Equipment ID to deactivate: ");
                        facade.deactivateEquipment(id);
                        ConsoleUtil.printSuccess("Equipment deactivated and retired from operational pool.");
                    }
                    case 5 -> renderMaintenanceTable(facade.viewGlobalMaintenanceLog(admin));
                    case 6 -> {
                        String reportId = util.readNonEmptyString("❯ Maintenance Ticket ID (e.g. REP-101): ");
                        String assignee = util.readNonEmptyString("❯ Assignee Staff Username: ");
                        facade.assignTask(admin, reportId, assignee);
                        ConsoleUtil.printSuccess("Ticket " + reportId + " successfully assigned to " + assignee);
                    }
                    case 7 -> {
                        String reportId = util.readNonEmptyString("❯ Maintenance Ticket ID to close: ");
                        facade.completeTask(admin, reportId);
                        ConsoleUtil.printSuccess("Ticket " + reportId + " marked COMPLETED. Apparatus restored to OPERATIONAL.");
                    }
                    case 8 -> registerUserFlow(util, facade);
                    case 0 -> inMenu = false;
                    default -> ConsoleUtil.printError("Invalid option! Please select a valid menu index.");
                }
            } catch (DuplicateEntityException | UnauthorizedAccessException | NoSuchElementException e) {
                ConsoleUtil.printError(e.getMessage());
            }
        }
    }

    private static void registerUserFlow(ConsoleUtil util, IWFCFacade facade) {
        System.out.println("\n" + DIM + "Account Type: 1. Instructor (Staff)   2. Member (Client)" + RESET);
        int roleChoice = util.readInt("❯ Select account type: ");
        String username = util.readNonEmptyString("❯ Desired Username: ");
        String name = util.readNonEmptyString("❯ Full Legal Name: ");
        try {
            User user = roleChoice == 1 ? new Instructor(username, name) : new Member(username, name);
            facade.registerUser(user);
            ConsoleUtil.printSuccess("Account registered successfully: " + user.getName() + " [" + user.getUsername() + "]");
        } catch (DuplicateEntityException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    // =========================================================================
    // 🏋️ INSTRUCTOR WORKFLOW
    // =========================================================================

    private static void instructorMenu(ConsoleUtil util, IWFCFacade facade, User instructor) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n" + CYAN + BOLD + "╔═══════════════════════════════════════════════════════════╗" + RESET);
            System.out.println(CYAN + BOLD + "║  🏋️  INSTRUCTOR PORTAL : " + String.format("%-33s", instructor.getName()) + "║" + RESET);
            System.out.println(CYAN + BOLD + "╠═══════════════════════════════════════════════════════════╣" + RESET);
            System.out.println("║  " + CYAN + "1." + RESET + " 📅 View My Scheduled Sessions                         ║");
            System.out.println("║  " + CYAN + "2." + RESET + " ➕ Schedule New Fitness Session (Conflict-Free)        ║");
            System.out.println("║  " + CYAN + "3." + RESET + " ⚠️ Report Equipment Mechanical/Electrical Fault        ║");
            System.out.println("║  " + CYAN + "4." + RESET + " ⏱️  Log Equipment Operating Hours (Preventative)      ║");
            System.out.println("║  " + RED + "0." + RESET + " 🔙 Logout to Main Login Screen                        ║");
            System.out.println(CYAN + BOLD + "╚═══════════════════════════════════════════════════════════╝" + RESET);

            int choice = util.readInt("❯ Select instructor action: ");
            try {
                switch (choice) {
                    case 1 -> renderSessionTable(facade.listSessionsByInstructor(instructor.getUsername()));
                    case 2 -> createSessionFlow(util, facade, instructor);
                    case 3 -> {
                        String reportId = util.readNonEmptyString("❯ Ticket ID (e.g. REP-101): ");
                        String equipmentId = util.readNonEmptyString("❯ Target Equipment ID: ");
                        String description = util.readNonEmptyString("❯ Fault Symptoms / Failure Description: ");
                        Urgency urgency = readUrgency(util);
                        facade.reportFault(reportId, equipmentId, description, urgency, instructor.getUsername());
                        ConsoleUtil.printSuccess("Fault ticket filed. Equipment transitioned to FAULTY state.");
                    }
                    case 4 -> {
                        String equipmentId = util.readNonEmptyString("❯ Target Equipment ID: ");
                        double hours = util.readDouble("❯ Session Operating Duration (Hours): ");
                        boolean alert = facade.logEquipmentUsage(equipmentId, hours);
                        ConsoleUtil.printSuccess("Run-hours logged successfully.");
                        if (alert) {
                            ConsoleUtil.printWarning("CRITICAL: Operating threshold exceeded! Maintenance recommended.");
                        }
                    }
                    case 0 -> inMenu = false;
                    default -> ConsoleUtil.printError("Invalid option! Please select a valid menu index.");
                }
            } catch (NoSuchElementException e) {
                ConsoleUtil.printError(e.getMessage());
            }
        }
    }

    private static void createSessionFlow(ConsoleUtil util, IWFCFacade facade, User instructor) {
        System.out.println("\n" + DIM + "--- Scheduling Wizard ---" + RESET);
        String id = util.readNonEmptyString("❯ Session ID (e.g. S-001): ");
        String title = util.readNonEmptyString("❯ Class Title (e.g. Morning HIIT Blast): ");
        String resource = util.readNonEmptyString("❯ Resource (Equipment ID or Studio Name): ");
        LocalDateTime start = util.readDateTime("❯ Session Start Time");
        int duration = util.readInt("❯ Session Duration in Minutes: ");
        boolean recurring = util.readYesNo("❯ Recurring weekly series?");
        int capacity = util.readInt("❯ Maximum Member Capacity: ");
        try {
            List<Session> created = facade.bookSession(id, title, resource, instructor.getUsername(),
                    start, duration, recurring, capacity);
            ConsoleUtil.printSuccess("Session successfully scheduled with zero collisions!");
            renderSessionTable(created);
        } catch (InvalidBookingException e) {
            ConsoleUtil.printError("Session Booking Rejected: " + e.getMessage());
        }
    }

    private static Urgency readUrgency(ConsoleUtil util) {
        System.out.println(DIM + "Urgency Level: 1. Low  2. Medium  3. High" + RESET);
        int choice = util.readInt("❯ Select Urgency: ");
        return switch (choice) {
            case 1 -> Urgency.LOW;
            case 3 -> Urgency.HIGH;
            default -> Urgency.MEDIUM;
        };
    }

    // =========================================================================
    // 👤 MEMBER WORKFLOW
    // =========================================================================

    private static void memberMenu(ConsoleUtil util, IWFCFacade facade, User member) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n" + GREEN + BOLD + "╔═══════════════════════════════════════════════════════════╗" + RESET);
            System.out.println(GREEN + BOLD + "║  👤 MEMBER PORTAL : " + String.format("%-37s", member.getName()) + "║" + RESET);
            System.out.println(GREEN + BOLD + "╠═══════════════════════════════════════════════════════════╣" + RESET);
            System.out.println("║  " + CYAN + "1." + RESET + " 📋 Browse Available Class Schedule                     ║");
            System.out.println("║  " + CYAN + "2." + RESET + " 🎟️  Book a Session Spot                                ║");
            System.out.println("║  " + CYAN + "3." + RESET + " ❌ Cancel Existing Reservation                        ║");
            System.out.println("║  " + CYAN + "4." + RESET + " 🔔 View Personal Notifications Mailbox                 ║");
            System.out.println("║  " + RED + "0." + RESET + " 🔙 Logout to Main Login Screen                        ║");
            System.out.println(GREEN + BOLD + "╚═══════════════════════════════════════════════════════════╝" + RESET);

            int choice = util.readInt("❯ Select member option: ");
            try {
                switch (choice) {
                    case 1 -> renderSessionTable(facade.listSessions());
                    case 2 -> {
                        String sessionId = util.readNonEmptyString("❯ Session ID to book: ");
                        facade.reserveSpot(sessionId, member.getUsername());
                        ConsoleUtil.printSuccess("Spot confirmed! You are officially enrolled.");
                    }
                    case 3 -> {
                        String sessionId = util.readNonEmptyString("❯ Session ID to cancel: ");
                        boolean cancelled = facade.cancelReservation(sessionId, member.getUsername());
                        if (cancelled) {
                            ConsoleUtil.printSuccess("Your reservation was cancelled.");
                        } else {
                            ConsoleUtil.printWarning("You did not hold an active reservation on that session.");
                        }
                    }
                    case 4 -> {
                        if (member instanceof Member m) {
                            List<String> notifications = m.getNotifications();
                            if (notifications.isEmpty()) {
                                ConsoleUtil.printInfo("Your notification mailbox is currently empty.");
                            } else {
                                System.out.println("\n" + BOLD + "🔔 RECENT NOTIFICATIONS:" + RESET);
                                notifications.forEach(n -> System.out.println("   • " + CYAN + n + RESET));
                            }
                        }
                    }
                    case 0 -> inMenu = false;
                    default -> ConsoleUtil.printError("Invalid option! Please select a valid menu index.");
                }
            } catch (NoSuchElementException | InvalidBookingException e) {
                ConsoleUtil.printError(e.getMessage());
            }
        }
    }

    // =========================================================================
    // 📊 MODERN TABULAR RENDERING
    // =========================================================================

    private static void renderEquipmentTable(List<Equipment> items) {
        if (items.isEmpty()) {
            ConsoleUtil.printInfo("No equipment records found.");
            return;
        }
        System.out.println("\n" + BOLD + "┌──────────┬─────────────────────────────┬──────────────────┬─────────────────┬──────────┐" + RESET);
        System.out.println(BOLD + "│ ID       │ Model / Commercial Name     │ Facility Zone    │ Status          │ Usage    │" + RESET);
        System.out.println(BOLD + "├──────────┼─────────────────────────────┼──────────────────┼─────────────────┼──────────┤" + RESET);
        for (Equipment eq : items) {
            System.out.printf("│ %-8s │ %-27s │ %-16s │ %-24s │ %6.1fh  │%n",
                    eq.getId(),
                    truncate(eq.getName(), 27),
                    truncate(eq.getLocation(), 16),
                    ConsoleUtil.statusBadge(eq.getStatus().name()),
                    eq.getUsageHours());
        }
        System.out.println(BOLD + "└──────────┴─────────────────────────────┴──────────────────┴─────────────────┴──────────┘" + RESET);
    }

    private static void renderSessionTable(List<Session> sessions) {
        if (sessions.isEmpty()) {
            ConsoleUtil.printInfo("No scheduled sessions currently found.");
            return;
        }
        System.out.println("\n" + BOLD + "┌──────────┬─────────────────────────────┬──────────────┬──────────────┬──────────────────┬──────────┐" + RESET);
        System.out.println(BOLD + "│ ID       │ Class Title                 │ Resource     │ Instructor   │ Start Time       │ Enrolled │" + RESET);
        System.out.println(BOLD + "├──────────┼─────────────────────────────┼──────────────┼──────────────┼──────────────────┼──────────┤" + RESET);
        for (Session s : sessions) {
            String enrolled = s.getBookedMemberUsernames().size() + " / " + s.getCapacity();
            System.out.printf("│ %-8s │ %-27s │ %-12s │ %-12s │ %-16s │ %8s │%n",
                    s.getId(),
                    truncate(s.getTitle(), 27),
                    truncate(s.getResourceName(), 12),
                    truncate(s.getInstructorUsername(), 12),
                    s.getStartTime().format(TIME_FMT),
                    enrolled);
        }
        System.out.println(BOLD + "└──────────┴─────────────────────────────┴──────────────┴──────────────┴──────────────────┴──────────┘" + RESET);
    }

    private static void renderMaintenanceTable(List<MaintenanceReport> reports) {
        if (reports.isEmpty()) {
            ConsoleUtil.printSuccess("No pending maintenance faults! All equipment is operating normally.");
            return;
        }
        System.out.println("\n" + BOLD + "┌──────────┬──────────────┬──────────┬─────────────────┬──────────────┬──────────────────────────────┐" + RESET);
        System.out.println(BOLD + "│ Ticket   │ Apparatus ID │ Urgency  │ Ticket Status   │ Assignee     │ Problem Description          │" + RESET);
        System.out.println(BOLD + "├──────────┼──────────────┼──────────┼─────────────────┼──────────────┼──────────────────────────────┤" + RESET);
        for (MaintenanceReport r : reports) {
            String assignee = r.getAssignedToUsername() != null ? r.getAssignedToUsername() : "-";
            System.out.printf("│ %-8s │ %-12s │ %-17s │ %-24s │ %-12s │ %-28s │%n",
                    r.getId(),
                    r.getEquipmentId(),
                    ConsoleUtil.statusBadge(r.getUrgency().name()),
                    ConsoleUtil.statusBadge(r.getStatus().name()),
                    truncate(assignee, 12),
                    truncate(r.getDescription(), 28));
        }
        System.out.println(BOLD + "└──────────┴──────────────┴──────────┴─────────────────┴──────────────┴──────────────────────────────┘" + RESET);
    }

    private static String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}
