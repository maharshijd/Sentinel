package main;

import model.SecurityRule;
import model.User;
import service.AuthService;
import dao.*;
import java.net.*;
import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static AuthService authService = new AuthService();
    private static AlertDAO alertDAO = new AlertDAO();
    private static EventDAO eventDAO = new EventDAO();
    private static DeviceDAO deviceDAO = new DeviceDAO();
    private static DeviceDetailsDAO deviceDetailsDAO = new DeviceDetailsDAO();
    private static SecurityRuleDAO securityRuleDAO = new SecurityRuleDAO();
    private static SessionDetailsDAO sessionDetailsDAO = new SessionDetailsDAO();
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

    private static boolean isAdmin() {
        return currentUser != null && currentUser.getRoleName().equalsIgnoreCase("ADMIN");
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
                if (currentUser == null) {
                    System.out.println("\n[ERROR] Invalid email or password.");
                } else {
                    System.out.println("\n[SUCCESS] Login successful. Welcome " + currentUser.getEmail());
                    int sessionId = sessionDetailsDAO.createSession(currentUser.getUserId(), detectLocalIpAddress());
                    if (sessionId > 0) {
                        System.out.println("[SESSION] Session ID: " + sessionId);
                    }
                }
                break;
            case 2:
                System.out.print("New Email: ");
                String newEmail = scanner.nextLine();
                System.out.print("New Password: ");
                String newPass = scanner.nextLine();
                System.out.print("Role ID (1=Admin, 2=Normal): ");
                int role = scanner.nextInt();
                System.out.print("Department ID: ");
                int dept = scanner.nextInt();
                if (authService.register(newEmail, newPass, role, dept)) {
                    System.out.println("[SUCCESS] Registration successful.");
                } else {
                    System.out.println("[ERROR] Registration failed.");
                }
                break;
            case 3:
                System.out.println("Shutting down SENTINEL.");
                System.exit(0);
            default:
                System.out.println("Invalid choice");
        }
    }

    private static void showDashboard() {
        System.out.println("\n--- " + currentUser.getRoleName() + " DASHBOARD ---");
        if (isAdmin()) {
            System.out.println("1 View Alerts");
            System.out.println("2 Resolve Alert");
            System.out.println("3 View Events");
            System.out.println("4 Simulate Event");
            System.out.println("5 Device Tracking");
            System.out.println("6 Session Details");
            System.out.println("7 Security Rules");
            System.out.println("8 Logout");
        } else {
            System.out.println("1 View Alerts");
            System.out.println("2 View Events");
            System.out.println("3 Device Tracking");
            System.out.println("4 Session Details");
            System.out.println("5 Logout");
        }
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        if (isAdmin()) {
            switch (choice) {
                case 1:
                    alertDAO.viewAllAlerts();
                    break;
                case 2:
                    alertDAO.viewAllAlerts();
                    System.out.print("Alert ID: ");
                    int alertId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Action taken: ");
                    String action = scanner.nextLine();
                    alertDAO.resolveAlert(alertId, currentUser.getUserId(), action);
                    break;
                case 3:
                    eventDAO.viewAllEvents();
                    break;
                case 4:
                    simulateSecurityEvent();
                    break;
                case 5:
                    showDeviceTrackingMenu();
                    break;
                case 6:
                    showSessionMenu();
                    break;
                case 7:
                    showRuleMenu();
                    break;
                case 8:
                    logout();
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        } else {
            switch (choice) {
                case 1:
                    alertDAO.viewAllAlerts();
                    break;
                case 2:
                    eventDAO.viewAllEvents();
                    break;
                case 3:
                    showDeviceTrackingMenu();
                    break;
                case 4:
                    showSessionMenu();
                    break;
                case 5:
                    logout();
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private static void logout() {
        currentUser = null;
        System.out.println("Logged out successfully.");
    }

    private static void simulateSecurityEvent() {
        System.out.print("Session ID: ");
        int sessionId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Event Type: ");
        String type = scanner.nextLine();
        System.out.print("Score: ");
        int score = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Severity: ");
        String severity = scanner.nextLine();
        int eventId = eventDAO.logEventAndGetId(sessionId, type, score, severity);
        if (eventId > 0) {
            evaluateRules(eventId, type, score);
        }
    }

    private static void evaluateRules(int eventId, String type, int score) {
        List<SecurityRule> rules = securityRuleDAO.getActiveRules();
        for (SecurityRule r : rules) {
            if (ruleMatch(r, type, score)) {
                alertDAO.createAlertFromEvent(eventId, r.getRuleName(), r.getSeverity());
            }
        }
    }

    private static boolean ruleMatch(SecurityRule r, String type, int score) {
        if (r.getMetricType().equalsIgnoreCase("EVENT_SCORE")) {
            int t = Integer.parseInt(r.getThresholdValue());
            if (r.getOperatorType().equals("GT"))
                return score > t;
        }
        return false;
    }

    private static void showRuleMenu() {
        System.out.println("1 Create Rule");
        System.out.println("2 View Rules");
        int c = scanner.nextInt();
        scanner.nextLine();
        if (c == 1) {
            System.out.print("Rule name: ");
            String name = scanner.nextLine();
            securityRuleDAO.createRule(name, "EVENT_SCORE", "GT", "50", "HIGH", currentUser.getUserId());
        } else {
            securityRuleDAO.viewAllRules();
        }
    }

    private static void showDeviceTrackingMenu() {
        System.out.println("Device fingerprint:");
        String f = scanner.nextLine();
        deviceDAO.registerOrUpdateDevice(currentUser.getUserId(), f, detectLocalIpAddress());
    }

    private static void showSessionMenu() {
        sessionDetailsDAO.viewSessionsByUser(currentUser.getUserId());
    }

    private static String detectLocalIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}