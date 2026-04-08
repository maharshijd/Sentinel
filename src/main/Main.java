package main;

import model.SecurityRule;
import model.User;
import service.AuthService;
import dao.AlertDAO;
import dao.DeviceDAO;
import dao.DeviceDetailsDAO;
import dao.EventDAO;
import dao.SecurityRuleDAO;
import dao.SessionDetailsDAO;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

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
                String email = scanner.nextLine().trim();
                System.out.print("Password: ");
                String pass = scanner.nextLine();

                if (!authService.isValidEmail(email)) {
                    System.out.println("\n[ERROR] Please enter a valid email address.");
                    break;
                }

                currentUser = authService.login(email, pass);

                if (currentUser == null) {
                    System.out.println("\n[ERROR] Invalid email or password.");
                } else {
                    System.out.println("\n[SUCCESS] Login successful. Welcome " + currentUser.getEmail());
                    int sessionId = sessionDetailsDAO.createSession(currentUser.getUserId(), detectLocalIpAddress());
                    if (sessionId > 0) {
                        System.out.println("[SESSION] Session details recorded with Session ID: " + sessionId);
                    }
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
                    System.out.println("[SUCCESS] Registration successful. You can now log in.");
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
        System.out.println("3. View Event Logs");
        System.out.println("4. Simulate Security Event");
        System.out.println("5. Track Connected Devices");
        System.out.println("6. Manage Session / Device Details");
        System.out.println("7. Set Security Rules");
        System.out.println("8. Logout");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

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
                eventDAO.viewAllEvents();
                break;
            case 4:
                simulateSecurityEvent();
                break;
            case 5:
                showDeviceTrackingMenu();
                break;
            case 6:
                showSessionAndDeviceDetailsMenu();
                break;
            case 7:
                if (currentUser.getRoleName().equalsIgnoreCase("ADMIN")) {
                    showSecurityRulesMenu();
                } else {
                    System.out.println("\n[DENIED] Only admins can manage security rules.");
                }
                break;
            case 8:
                currentUser = null;
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void simulateSecurityEvent() {
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

        int eventId = eventDAO.logEventAndGetId(sessionId, eventType, score, severity.toUpperCase());
        if (eventId > 0) {
            System.out.println("\n[SUCCESS] Security event logged successfully.");
            evaluateRulesForEvent(eventId, eventType, score);
        } else {
            System.out.println("\n[ERROR] Failed to log event.");
        }
    }

    private static void evaluateRulesForEvent(int eventId, String eventType, int score) {
        List<SecurityRule> rules = securityRuleDAO.getActiveRules();
        int createdAlerts = 0;

        for (SecurityRule rule : rules) {
            if (ruleMatchesEvent(rule, eventType, score)) {
                String alertType = "RULE_MATCH: " + rule.getRuleName();
                if (alertDAO.createAlertFromEvent(eventId, alertType, rule.getSeverity())) {
                    createdAlerts++;
                }
            }
        }

        if (createdAlerts > 0) {
            System.out.println("[RULE ENGINE] " + createdAlerts + " alert(s) created from active security rules.");
        }
    }

    private static boolean ruleMatchesEvent(SecurityRule rule, String eventType, int score) {
        String metric = rule.getMetricType().toUpperCase();
        String operator = rule.getOperatorType().toUpperCase();
        String threshold = rule.getThresholdValue();

        if (metric.equals("EVENT_SCORE")) {
            int thresholdInt;
            try {
                thresholdInt = Integer.parseInt(threshold);
            } catch (NumberFormatException e) {
                return false;
            }

            switch (operator) {
                case "GT":
                    return score > thresholdInt;
                case "GTE":
                    return score >= thresholdInt;
                case "EQ":
                    return score == thresholdInt;
                case "LT":
                    return score < thresholdInt;
                case "LTE":
                    return score <= thresholdInt;
                default:
                    return false;
            }
        }

        if (metric.equals("EVENT_TYPE")) {
            switch (operator) {
                case "EQ":
                    return eventType.equalsIgnoreCase(threshold);
                case "CONTAINS":
                    return eventType.toUpperCase().contains(threshold.toUpperCase());
                default:
                    return false;
            }
        }

        return false;
    }

    private static void showSecurityRulesMenu() {
        while (true) {
            System.out.println("\n--- SECURITY RULES ---");
            System.out.println("1. Create Rule");
            System.out.println("2. View Rules");
            System.out.println("3. Enable/Disable Rule");
            System.out.println("4. Back to Dashboard");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Rule Name: ");
                    String ruleName = scanner.nextLine();

                    System.out.print("Metric Type (EVENT_SCORE / EVENT_TYPE): ");
                    String metricType = scanner.nextLine().toUpperCase();

                    System.out.print("Operator (GT/GTE/EQ/LT/LTE/CONTAINS): ");
                    String operator = scanner.nextLine().toUpperCase();

                    System.out.print("Threshold Value (e.g., 70 or UNAUTHORIZED_ACCESS): ");
                    String threshold = scanner.nextLine();

                    System.out.print("Severity (LOW/MEDIUM/HIGH/CRITICAL): ");
                    String severity = scanner.nextLine().toUpperCase();

                    if (securityRuleDAO.createRule(ruleName, metricType, operator, threshold, severity,
                            currentUser.getUserId())) {
                        System.out.println("[SUCCESS] Security rule created.");
                    } else {
                        System.out.println("[ERROR] Could not create rule.");
                    }
                    break;

                case 2:
                    securityRuleDAO.viewAllRules();
                    break;

                case 3:
                    securityRuleDAO.viewAllRules();
                    System.out.print("Enter Rule ID: ");
                    int ruleId = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Set status (1 = ACTIVE, 2 = INACTIVE): ");
                    int statusChoice = scanner.nextInt();
                    scanner.nextLine();

                    boolean active = statusChoice == 1;
                    if (securityRuleDAO.setRuleStatus(ruleId, active)) {
                        System.out.println("[SUCCESS] Rule status updated.");
                    } else {
                        System.out.println("[ERROR] Could not update rule status.");
                    }
                    break;

                case 4:
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void showDeviceTrackingMenu() {
        boolean isAdmin = currentUser.getRoleName().equalsIgnoreCase("ADMIN");

        while (true) {
            System.out.println("\n--- DEVICE TRACKING ---");
            System.out.println("1. Register / Update Current Device");
            System.out.println("2. View Connected Devices");
            System.out.println("3. Disconnect a Device");
            System.out.println("4. Back to Dashboard");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Device Fingerprint (unique id): ");
                    String fingerprint = scanner.nextLine();
                    String ipAddress = detectLocalIpAddress();
                    System.out.println("Detected IP Address: " + ipAddress);

                    if (deviceDAO.registerOrUpdateDevice(currentUser.getUserId(), fingerprint, ipAddress)) {
                        System.out.println("[SUCCESS] Device tracked successfully.");
                    } else {
                        System.out.println("[ERROR] Could not track this device.");
                    }
                    break;

                case 2:
                    if (isAdmin) {
                        deviceDAO.viewAllConnectedDevices();
                    } else {
                        deviceDAO.viewConnectedDevicesByUser(currentUser.getUserId());
                    }
                    break;

                case 3:
                    System.out.print("Enter Device ID to disconnect: ");
                    int deviceId = scanner.nextInt();
                    scanner.nextLine();

                    boolean disconnected;
                    if (isAdmin) {
                        disconnected = deviceDAO.disconnectDeviceAsAdmin(deviceId);
                    } else {
                        disconnected = deviceDAO.disconnectOwnDevice(deviceId, currentUser.getUserId());
                    }

                    if (disconnected) {
                        System.out.println("[SUCCESS] Device disconnected.");
                    } else {
                        System.out.println("[ERROR] Disconnect failed. Check Device ID/permissions.");
                    }
                    break;

                case 4:
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void showSessionAndDeviceDetailsMenu() {
        boolean isAdmin = currentUser.getRoleName().equalsIgnoreCase("ADMIN");

        while (true) {
            System.out.println("\n--- SESSION / DEVICE DETAILS ---");
            System.out.println("1. Add Device Details");
            System.out.println("2. View All Device Details");
            System.out.println("3. View My Session Details");
            System.out.println("4. Back to Dashboard");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter MAC Address: ");
                    String macAddress = scanner.nextLine();
                    System.out.print("Enter OS Version: ");
                    String osVersion = scanner.nextLine();

                    int deviceId = deviceDetailsDAO.saveDeviceDetails(macAddress, osVersion);
                    if (deviceId > 0) {
                        System.out.println("[SUCCESS] Device details saved with Device ID: " + deviceId);
                    } else {
                        System.out.println("[ERROR] Could not save device details.");
                    }
                    break;

                case 2:
                    if (isAdmin) {
                        deviceDetailsDAO.viewAllDevices();
                    } else {
                        System.out.println("\n[DENIED] Only admins can view all device details.");
                    }
                    break;

                case 3:
                    sessionDetailsDAO.viewSessionsByUser(currentUser.getUserId());
                    break;

                case 4:
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static String detectLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isUp() == false || networkInterface.isLoopback()
                        || networkInterface.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && address.isLoopbackAddress() == false) {
                        return address.getHostAddress();
                    }
                }
            }

            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            return "UNKNOWN";
        }
    }
}
