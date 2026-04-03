package dao;

import config.DBconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlertDAO {

    public void viewAllAlerts() {
        String query = "SELECT a.alert_id, a.alert_type, a.security_level, a.alert_status, e.event_score " +
                "FROM alerts a " +
                "JOIN event_logs e ON a.event_id = e.event_id " +
                "ORDER BY a.created_time DESC";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            System.out.println("\n=======================================================================");
            System.out.println("                          ACTIVE SECURITY ALERTS                       ");
            System.out.println("=======================================================================");
            System.out.printf("%-10s | %-25s | %-12s | %-10s | %-6s%n",
                    "ALERT ID", "ALERT TYPE", "SEC LEVEL", "STATUS", "SCORE");
            System.out.println("-----------------------------------------------------------------------");

            boolean hasAlerts = false;
            while (rs.next()) {
                hasAlerts = true;
                System.out.printf("%-10d | %-25s | %-12s | %-10s | %-6d%n",
                        rs.getInt("alert_id"),
                        rs.getString("alert_type"),
                        rs.getString("security_level"),
                        rs.getString("alert_status"),
                        rs.getInt("event_score"));
            }

            if (!hasAlerts) {
                System.out.println("No alerts found in the system.");
            }
            System.out.println("=======================================================================\n");

        } catch (SQLException e) {
            System.out.println("Error fetching alerts from the database.");
            e.printStackTrace();
        }
    }

    // NEW METHOD: Resolves an alert and logs the action in the remediation table
    public boolean resolveAlert(int alertId, int currentUserId, String actionTaken) {
        String updateAlertQuery = "UPDATE alerts SET alert_status = 'resolved' WHERE alert_id = ?";
        String insertRemediationQuery = "INSERT INTO remediation (alert_id, admin_id, status, action_taken, resolved_time) "
                +
                "VALUES (?, (SELECT admin_id FROM admin WHERE user_id = ?), 'resolved', ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DBconnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction so both queries succeed or fail together

            try (PreparedStatement updateStmt = conn.prepareStatement(updateAlertQuery);
                    PreparedStatement insertStmt = conn.prepareStatement(insertRemediationQuery)) {

                // 1. Update Alert Status
                updateStmt.setInt(1, alertId);
                int rowsUpdated = updateStmt.executeUpdate();

                if (rowsUpdated > 0) {
                    // 2. Log Remediation Action
                    insertStmt.setInt(1, alertId);
                    insertStmt.setInt(2, currentUserId);
                    insertStmt.setString(3, actionTaken);
                    insertStmt.executeUpdate();

                    conn.commit(); // Commit the transaction
                    return true;
                }
            } catch (SQLException ex) {
                conn.rollback(); // Undo if something goes wrong
                System.out.println("Error processing resolution. Transaction rolled back.");
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}