package main;

import model.User;
import service.AuthService;
import dao.AlertDAO;
import dao.EventDAO;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static AuthService authService = new AuthService();
    private static AlertDAO alertDAO = new AlertDAO();
    private static EventDAO eventDAO = new EventDAO(); // <-- New Event DAO added
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("      SENTINEL CYBERSECURITY MONITORING SYSTEM    ");
        System.out.println("==================================================");

        while (true) {
            if (currentUser == null) {
                showMainMenu();
            } else {
                showDashboard();
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Login");
        System.out.println("2. Register User");
        System.out.println("3. Exit");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("Email: ");
                String email = scanner.nextLine();
                System.out.print("Password: ");
                String pass = scanner.nextLine();

                currentUser = authService.login(email, pass);

                if (currentUser != null) {
                    System.out.println("\n[SUCCESS] Login successful! Welcome " + currentUser.getEmail());
                } else {
                    System.out.println("\n[ERROR] Invalid email or password.");
                }
                break;
            case 2:
                System.out.print("New Email: ");
                String newEmail = scanner.nextLine();
                System.out.print("New Password: ");
                String newPass = scanner.nextLine();
                System.out.print("Role ID (1 = Admin, 2 = Normal): ");
                int role = scanner.nextInt();
                System.out.print("Department ID (1 = IT Security, 2 = HR): ");
                int dept = scanner.nextInt();

                if (authService.register(newEmail, newPass, role, dept)) {
                    System.out.println("[SUCCESS] Registration successful! You can now log in.");
                } else {
                    System.out.println("[ERROR] Registration failed.");
                }
                break;
            case 3:
                System.out.println("Shutting down SENTINEL. Goodbye.");
                System.exit(0);
            default:
                System.out.println("Invalid choice. Try again.");
        }
    }

    private static void showDashboard() {
        System.out.println("\n--- " + currentUser.getRoleName() + " DASHBOARD ---");
        System.out.println("1. View Active Alerts");
        System.out.println("2. Resolve an Alert");
        System.out.println("3. View Event Logs"); // <-- New Option
        System.out.println("4. Simulate Security Event"); // <-- New Option
        System.out.println("5. Logout");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                alertDAO.viewAllAlerts();
                break;
            case 2:
                if (currentUser.getRoleName().equalsIgnoreCase("ADMIN")) {
                    alertDAO.viewAllAlerts();
                    System.out.print("Enter the Alert ID you want to resolve: ");
                    int alertId = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter action taken to resolve this threat: ");
                    String actionTaken = scanner.nextLine();

                    if (alertDAO.resolveAlert(alertId, currentUser.getUserId(), actionTaken)) {
                        System.out.println("\n[SUCCESS] Alert " + alertId + " marked as resolved.");
                    } else {
                        System.out.println("\n[ERROR] Failed to resolve alert.");
                    }
                } else {
                    System.out.println("\n[DENIED] You do not have admin privileges to resolve alerts.");
                }
                break;
            case 3:
                eventDAO.viewAllEvents(); // <-- Triggers Event Viewer
                break;
            case 4:
                // Simulate an event being logged
                System.out.print("Enter Session ID (try 1): ");
                int sessionId = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Enter Event Type (e.g., UNAUTHORIZED_ACCESS): ");
                String eventType = scanner.nextLine();

                System.out.print("Enter Threat Score (0-100): ");
                int score = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Enter Severity (LOW/MEDIUM/HIGH/CRITICAL): ");
                String severity = scanner.nextLine();

                if (eventDAO.logEvent(sessionId, eventType, score, severity.toUpperCase())) {
                    System.out.println("\n[SUCCESS] Security event logged successfully.");
                } else {
                    System.out.println("\n[ERROR] Failed to log event.");
                }
                break;
            case 5:
                currentUser = null;
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
}