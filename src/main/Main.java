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
            if (currentUser == null)
                showMainMenu();
            else
                showDashboard();
        }
    }

    private static boolean isAdmin() {
        return currentUser != null && currentUser.getRoleName().equalsIgnoreCase("ADMIN");
    }

    private static void showMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1 Login");
        System.out.println("2 Register User");
        System.out.println("3 Exit");
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1:
                loginUser();
                break;
            case 2:
                registerUser();
                break;
            case 3:
                System.out.println("System shutting down");
                System.exit(0);
            default:
                System.out.println("Invalid choice");
        }
    }

    private static void loginUser() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();
        User tempUser = authService.login(email, pass);
        if (tempUser == null) {
            System.out.println("[ERROR] Invalid email or password");
            int eventId = eventDAO.logEventAndGetId(1, "LOGIN_FAILED", 80, "HIGH");
            if (eventId > 0)
                evaluateRulesForEvent(eventId, "LOGIN_FAILED", 80);
        } else {
            currentUser = tempUser;
            System.out.println("[SUCCESS] Welcome " + currentUser.getEmail());
            int sessionId = sessionDetailsDAO.createSession(currentUser.getUserId(), detectLocalIpAddress());
            if (sessionId > 0)
                System.out.println("Session ID: " + sessionId);
        }
    }

    private static void registerUser() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String pass = scanner.nextLine();
        System.out.print("Role (1=Admin 2=User): ");
        int role = scanner.nextInt();
        System.out.print("Department id: ");
        int dept = scanner.nextInt();
        if (authService.register(email, pass, role, dept))
            System.out.println("User created");
        else
            System.out.println("Registration failed");
    }

    private static void showDashboard() {
        System.out.println("\n--- " + currentUser.getRoleName() + " DASHBOARD ---");
        if (isAdmin()) {
            System.out.println("1 View Alerts");
            System.out.println("2 Resolve Alert");
            System.out.println("3 View Events");
            System.out.println("4 Simulate Event");
            System.out.println("5 Sessions");
            System.out.println("6 Rules");
            System.out.println("7 Logout");
        } else {
            System.out.println("1 View Alerts");
            System.out.println("2 View Events");
            System.out.println("3 Sessions");
            System.out.println("4 Logout");
        }
        System.out.print("Choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        if (isAdmin())
            adminActions(choice);
        else
            userActions(choice);
    }

    private static void adminActions(int c) {
        switch (c) {
            case 1:
                alertDAO.viewAllAlerts();
                break;
            case 2:
                resolveAlert();
                break;
            case 3:
                eventDAO.viewAllEvents();
                break;
            case 4:
                simulateSecurityEvent();
                break;
            case 5:
                sessionDetailsDAO.viewSessionsByUser(currentUser.getUserId());
                break;
            case 6:
                showSecurityRulesMenu();
                break;
            case 8:
                logout();
                break;
            default:
                System.out.println("Invalid");
        }
    }

    private static void userActions(int c) {
        switch (c) {
            case 1:
                alertDAO.viewAllAlerts();
                break;
            case 2:
                eventDAO.viewAllEvents();
                break;
            case 3:
                sessionDetailsDAO.viewSessionsByUser(currentUser.getUserId());
                break;
            case 4:
                logout();
                break;
            default:
                System.out.println("Invalid");
        }
    }

    private static void resolveAlert() {
        alertDAO.viewAllAlerts();
        System.out.print("Alert id: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Action: ");
        String action = scanner.nextLine();
        alertDAO.resolveAlert(id, currentUser.getUserId(), action);
    }

    private static void simulateSecurityEvent() {
        System.out.print("Session id: ");
        int sid = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Event type: ");
        String type = scanner.nextLine();
        System.out.print("Score: ");
        int score = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Severity: ");
        String severity = scanner.nextLine();
        int eventId = eventDAO.logEventAndGetId(sid, type, score, severity);
        if (eventId > 0)
            evaluateRulesForEvent(eventId, type, score);
    }

    private static void evaluateRulesForEvent(int eventId, String type, int score) {
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
            if (r.getOperatorType().equals("GTE"))
                return score >= t;
        }
        if (r.getMetricType().equalsIgnoreCase("EVENT_TYPE")) {
            return type.equalsIgnoreCase(r.getThresholdValue());
        }
        return false;
    }

    private static void showSecurityRulesMenu() {
        System.out.println("1 Create rule");
        System.out.println("2 View rules");
        int c = scanner.nextInt();
        scanner.nextLine();
        if (c == 1) {
            System.out.print("Rule name: ");
            String name = scanner.nextLine();
            System.out.print("Metric type: ");
            String metric = scanner.nextLine();
            System.out.print("Operator: ");
            String op = scanner.nextLine();
            System.out.print("Threshold: ");
            String val = scanner.nextLine();
            System.out.print("Severity: ");
            String sev = scanner.nextLine();
            securityRuleDAO.createRule(name, metric, op, val, sev, currentUser.getUserId());
        } else {
            securityRuleDAO.viewAllRules();
        }
    }

    private static void showDeviceTrackingMenu() {
        System.out.print("Fingerprint: ");
        String f = scanner.nextLine();
        deviceDAO.registerOrUpdateDevice(currentUser.getUserId(), f, detectLocalIpAddress());
    }

    private static void logout() {
        currentUser = null;
        System.out.println("Logged out");
    }

    private static String detectLocalIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}