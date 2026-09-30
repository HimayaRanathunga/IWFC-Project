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

// Static import: lets us write CYAN, RESET, BOLD etc. without the ConsoleUtil. prefix.
import static com.iwfc.ui.ConsoleUtil.*;

/**
 * Modern, styled Console Entry Point for IWFC.
 * Employs pixel-perfect column alignment, ANSI color styling, and role-based navigation.
 *
 * [Console Interface] Technical Requirement: console user interface.
 * Design patterns: uses the Singleton (SystemManager.getInstance()) and talks only to the Facade (IWFCFacade),
 * so the UI never touches services or repositories directly.
 * Polymorphism: each Role gets its own menu, and User objects are handled through the User base type.
 */
public class ConsoleApp {

    // Shared immutable formatter (DateTimeFormatter is thread-safe), reused by the session table.
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Entry point wiring: Singleton (one shared SystemManager) is passed into the Facade (single simple API for the UI).
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
            // Enhanced switch on an enum (Role): arrow cases, no fall-through, no break needed.
            // Polymorphic behaviour by Role: each role is sent to a different menu.
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
        System.out.println(CYAN + "╚══════════════════════════════════════════════════════════════════╝" + RESET);
    }

    // Role selection and login. Returns null when the user chooses to exit (ends the main loop).
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
        // Enhanced switch statement with arrow cases; each branch assigns the Role or returns/recurses.
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

        // Only show accounts of the chosen role (Facade returns a List<User>, Generics/Collections).
        System.out.println("\n" + DIM + "Available registered accounts:" + RESET);
        // Lambda + forEach: prints each candidate account.
        candidates.forEach(u -> System.out.println("   • " + BOLD + u.getUsername() + RESET + " (" + u.getName() + ")"));

        String username = util.readNonEmptyString("❯ Enter username: ");
        // Optional chain: filter by role, otherwise orElseGet lambda shows an error and retries the login.
        return facade.findUser(username)
                .filter(u -> u.getRole() == role)
                .orElseGet(() -> {
                    ConsoleUtil.printError("Username not found for role " + role + ". Please try again.");
                    return login(util, facade);
                });
    }

    // Administrator menu (RBAC: admin-only actions; the facade re-checks the role for protected calls).
    // =========================================================================
    // ADMINISTRATOR WORKFLOW
    // =========================================================================

    private static void adminMenu(ConsoleUtil util, IWFCFacade facade, User admin) {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n" + MAGENTA + BOLD + "╔═══════════════════════════════════════════════════════════╗" + RESET);
            System.out.println(MAGENTA + BOLD + "║  👑 ADMINISTRATOR PORTAL : " + String.format("%-31s", admin.getName()) + "║" + RESET);
            System.out.println(MAGENTA + BOLD + "╠═══════════════════════════════════════════════════════════╣" + RESET);
            System.out.println("║  " + CYAN + "1." + RESET + " 📋 View Equipment Inventory (Tabular)                 ║");
            System.out.println("║  " + CYAN + "2." + RESET + " ➕ Register New Equipment                            ║");
            System.out.println("║  " + CYAN + "3." + RESET + " ✏️  Edit Equipment Details                          ║");
            System.out.println("║  " + CYAN + "4." + RESET + " 🚫 Deactivate Equipment                               ║");
            System.out.println("║  " + CYAN + "5." + RESET + " 🛠️  View Global Maintenance Tickets                   ║");
            System.out.println("║  " + CYAN + "6." + RESET + " 👷 Assign Maintenance Ticket                          ║");
            System.out.println("║  " + CYAN + "7." + RESET + " ✅ Complete Maintenance Ticket                        ║");
            System.out.println("║  " + CYAN + "8." + RESET + " 👥 Register Instructor / Member Account              ║");
            System.out.println("║  " + RED + "0." + RESET + " 🔙 Logout to Main Login Screen                        ║");
            System.out.println(MAGENTA + BOLD + "╚═══════════════════════════════════════════════════════════╝" + RESET);

            int choice = util.readInt("❯ Select administrative action: ");
            // Input validation happens in ConsoleUtil; here we only handle business errors from the Facade.
            try {
                switch (choice) {
                    // Enhanced switch on int menu choice; case blocks use { } for local variables.
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
                    // Administrator is passed in so the Facade can enforce role-based access (UnauthorizedAccessException).
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
            // Multi-catch: one handler for three different exception types (custom unchecked + NoSuchElementException).
            } catch (DuplicateEntityException | UnauthorizedAccessException | NoSuchElementException e) {
                ConsoleUtil.printError(e.getMessage());
            }
        }
    }

    // Account registration flow (admin only): creates a new Instructor or Member.
    private static void registerUserFlow(ConsoleUtil util, IWFCFacade facade) {
        System.out.println("\n" + DIM + "Account Type: 1. Instructor (Staff)   2. Member (Client)" + RESET);
        int roleChoice = util.readInt("❯ Select account type: ");
        String username = util.readNonEmptyString("❯ Desired Username: ");
        String name = util.readNonEmptyString("❯ Full Legal Name: ");
        try {
            // Polymorphism: Instructor and Member are both Users, so the ternary result is stored as the base type User.
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

    // Instructor menu: scheduling sessions, fault reports and usage logging.
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

    // Session scheduling wizard: InvalidBookingException (checked) must be handled here, it covers double booking and operating hours.
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

    // Switch expression: returns a value directly; default handles any other number as MEDIUM.
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

    // Member menu: browse, book and cancel sessions, and read Observer notifications.
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
                        // Pattern-matching instanceof: checks the type and binds it to "m" in one step (no cast).
                        // Observer: Member stores notifications pushed by the NotificationCenter.
                        if (member instanceof Member m) {
                            List<String> notifications = m.getNotifications();
                            if (notifications.isEmpty()) {
                                ConsoleUtil.printInfo("Your notification mailbox is currently empty.");
                            } else {
                                System.out.println("\n" + BOLD + "🔔 RECENT NOTIFICATIONS:" + RESET);
                                // Lambda + forEach: prints every notification.
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
    // 📊 PIXEL-PERFECT ALIGNED TABULAR RENDERING
    // =========================================================================

    // Table helpers. Padding is done on the plain text; the result is only coloured afterwards.
    // Reason: ANSI escape codes count as characters in String.format, so colouring first would break column widths.
    // pad = left aligned (text columns), padRight = right aligned (number columns). Long text is cut with "...".
    private static String pad(String text, int width) {
        if (text == null) text = "";
        if (text.length() > width) {
            text = text.substring(0, width - 3) + "...";
        }
        return String.format("%-" + width + "s", text);
    }

    private static String padRight(String text, int width) {
        if (text == null) text = "";
        if (text.length() > width) {
            text = text.substring(0, width - 3) + "...";
        }
        return String.format("%" + width + "s", text);
    }

    // Switch expression on strings: maps a status/urgency name to an ANSI colour.
    private static String formatStatusCell(String status, int width) {
        String color = switch (status.toUpperCase()) {
            case "OPERATIONAL", "COMPLETED" -> GREEN + BOLD;
            case "FAULTY", "HIGH" -> RED + BOLD;
            case "UNDER_MAINTENANCE", "PENDING", "MEDIUM" -> YELLOW + BOLD;
            case "ASSIGNED" -> CYAN + BOLD;
            default -> WHITE;
        };
        // Pad raw text first, then wrap in ANSI color tags so padding is not corrupted by escape codes
        return color + pad(status, width) + RESET;
    }

    // Table renderer for Equipment (Task 1 view). Each row is built from getters, columns share fixed widths.
    private static void renderEquipmentTable(List<Equipment> items) {
        if (items.isEmpty()) {
            ConsoleUtil.printInfo("No equipment records found.");
            return;
        }
        // Column widths: ID: 8, Model: 27, Location: 16, Status: 17, Usage: 8
        System.out.println("\n" + BOLD + "┌──────────┬─────────────────────────────┬──────────────────┬───────────────────┬──────────┐" + RESET);
        System.out.println(BOLD + "│ ID       │ Model / Commercial Name     │ Facility Zone    │ Status            │ Usage    │" + RESET);
        System.out.println(BOLD + "├──────────┼─────────────────────────────┼──────────────────┼───────────────────┼──────────┤" + RESET);
        for (Equipment eq : items) {
            String usageStr = String.format("%.1fh", eq.getUsageHours());
            System.out.println("│ " + pad(eq.getId(), 8) +
                               " │ " + pad(eq.getName(), 27) +
                               " │ " + pad(eq.getLocation(), 16) +
                               " │ " + formatStatusCell(eq.getStatus().name(), 17) +
                               " │ " + padRight(usageStr, 8) + " │");
        }
        System.out.println(BOLD + "└──────────┴─────────────────────────────┴──────────────────┴───────────────────┴──────────┘" + RESET);
    }

    // Table renderer for Session (Task 2 view): shows enrolled count as "booked / capacity".
    private static void renderSessionTable(List<Session> sessions) {
        if (sessions.isEmpty()) {
            ConsoleUtil.printInfo("No scheduled sessions currently found.");
            return;
        }
        // Column widths: ID: 8, Title: 27, Resource: 14, Instructor: 12, Time: 16, Enrolled: 8
        System.out.println("\n" + BOLD + "┌──────────┬─────────────────────────────┬────────────────┬──────────────┬──────────────────┬──────────┐" + RESET);
        System.out.println(BOLD + "│ ID       │ Class Title                 │ Resource       │ Instructor   │ Start Time       │ Enrolled │" + RESET);
        System.out.println(BOLD + "├──────────┼─────────────────────────────┼────────────────┼──────────────┼──────────────────┼──────────┤" + RESET);
        for (Session s : sessions) {
            String enrolled = s.getBookedMemberUsernames().size() + " / " + s.getCapacity();
            System.out.println("│ " + pad(s.getId(), 8) +
                               " │ " + pad(s.getTitle(), 27) +
                               " │ " + pad(s.getResourceName(), 14) +
                               " │ " + pad(s.getInstructorUsername(), 12) +
                               " │ " + pad(s.getStartTime().format(TIME_FMT), 16) +
                               " │ " + padRight(enrolled, 8) + " │");
        }
        System.out.println(BOLD + "└──────────┴─────────────────────────────┴────────────────┴──────────────┴──────────────────┴──────────┘" + RESET);
    }

    // Table renderer for MaintenanceReport (Task 3 view); unassigned tickets show "-" as assignee.
    private static void renderMaintenanceTable(List<MaintenanceReport> reports) {
        if (reports.isEmpty()) {
            ConsoleUtil.printSuccess("No pending maintenance faults! All equipment is operating normally.");
            return;
        }
        // Column widths: Ticket: 8, EquipID: 10, Urgency: 9, Status: 14, Assignee: 12, Problem: 28
        System.out.println("\n" + BOLD + "┌──────────┬────────────┬───────────┬────────────────┬──────────────┬──────────────────────────────┐" + RESET);
        System.out.println(BOLD + "│ Ticket   │ Equip ID   │ Urgency   │ Status         │ Assignee     │ Problem Description          │" + RESET);
        System.out.println(BOLD + "├──────────┼────────────┼───────────┼────────────────┼──────────────┼──────────────────────────────┤" + RESET);
        for (MaintenanceReport r : reports) {
            String assignee = r.getAssignedToUsername() != null ? r.getAssignedToUsername() : "-";
            System.out.println("│ " + pad(r.getId(), 8) +
                               " │ " + pad(r.getEquipmentId(), 10) +
                               " │ " + formatStatusCell(r.getUrgency().name(), 9) +
                               " │ " + formatStatusCell(r.getStatus().name(), 14) +
                               " │ " + pad(assignee, 12) +
                               " │ " + pad(r.getDescription(), 28) + " │");
        }
        System.out.println(BOLD + "└──────────┴────────────┴───────────┴────────────────┴──────────────┴──────────────────────────────┘" + RESET);
    }
}
