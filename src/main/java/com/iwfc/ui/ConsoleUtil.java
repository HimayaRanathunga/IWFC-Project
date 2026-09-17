package com.iwfc.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Modern console utility providing ANSI color accents, box-drawing styling,
 * status badge rendering, and robust input validation.
 */
public class ConsoleUtil {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // --- ANSI Escape Codes for Modern CLI Aesthetics ---
    public static final String RESET   = "\u001B[0m";
    public static final String BOLD    = "\u001B[1m";
    public static final String DIM     = "\u001B[2m";

    public static final String RED     = "\u001B[31m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String BLUE    = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN    = "\u001B[36m";
    public static final String WHITE   = "\u001B[37m";

    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_DARK = "\u001B[100m";

    private final Scanner scanner;

    public ConsoleUtil(Scanner scanner) {
        this.scanner = scanner;
    }

    public static void printSuccess(String message) {
        System.out.println(GREEN + " ✔  " + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(RED + BOLD + " ✖  [ERROR] " + RESET + RED + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + BOLD + " ⚠  [WARNING] " + RESET + YELLOW + message + RESET);
    }

    public static void printInfo(String message) {
        System.out.println(CYAN + " ℹ  " + message + RESET);
    }

    public static String statusBadge(String status) {
        return switch (status.toUpperCase()) {
            case "OPERATIONAL" -> GREEN + BOLD + "[ OPERATIONAL ]" + RESET;
            case "FAULTY" -> RED + BOLD + "[   FAULTY    ]" + RESET;
            case "UNDER_MAINTENANCE" -> YELLOW + BOLD + "[ MAINTENANCE ]" + RESET;
            case "PENDING" -> YELLOW + BOLD + "[   PENDING   ]" + RESET;
            case "ASSIGNED" -> CYAN + BOLD + "[  ASSIGNED   ]" + RESET;
            case "COMPLETED" -> GREEN + BOLD + "[  COMPLETED  ]" + RESET;
            case "HIGH" -> RED + BOLD + "[ HIGH ]" + RESET;
            case "MEDIUM" -> YELLOW + BOLD + "[ MED  ]" + RESET;
            case "LOW" -> GREEN + BOLD + "[ LOW  ]" + RESET;
            default -> "[" + status + "]";
        };
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(CYAN + prompt + RESET);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                printError("Please enter a valid whole number.");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            System.out.print(CYAN + prompt + RESET);
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                printError("Please enter a valid decimal number.");
            }
        }
    }

    public String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(CYAN + prompt + RESET);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            printWarning("This field cannot be empty. Please enter a value.");
        }
    }

    public LocalDateTime readDateTime(String prompt) {
        while (true) {
            System.out.print(CYAN + prompt + " (yyyy-MM-dd HH:mm): " + RESET);
            String line = scanner.nextLine().trim();
            try {
                return LocalDateTime.parse(line, DATE_TIME_FORMAT);
            } catch (DateTimeParseException e) {
                printError("Invalid format! Use yyyy-MM-dd HH:mm (e.g. 2026-03-25 09:00)");
            }
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(CYAN + prompt + " (y/n): " + RESET);
            String line = scanner.nextLine().trim().toLowerCase();
            if (line.equals("y") || line.equals("yes")) return true;
            if (line.equals("n") || line.equals("no")) return false;
            printWarning("Please answer 'y' for yes or 'n' for no.");
        }
    }
}
