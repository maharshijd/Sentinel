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

    public boolean createAlertFromEvent(int eventId, String alertType, String securityLevel) {

        String query = "INSERT INTO alerts (event_id, alert_type, security_level, alert_status, created_time) " +
                "VALUES (?, ?, ?, 'active', CURRENT_TIMESTAMP)";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, eventId);
            stmt.setString(2, alertType);
            stmt.setString(3, securityLevel.toUpperCase());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error creating alert from rule.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean resolveAlert(int alertId, int currentUserId, String actionTaken) {

        String checkAdminQuery = "SELECT admin_id FROM admin WHERE user_id=?";

        String updateAlertQuery = "UPDATE alerts SET alert_status='resolved' WHERE alert_id=? AND alert_status='active'";

        String insertRemediationQuery = "INSERT INTO remediation(alert_id,admin_id,status,action_taken,resolved_time) VALUES(?,?, 'resolved',?,CURRENT_TIMESTAMP)";

        try (Connection conn = DBconnection.getConnection()) {

            conn.setAutoCommit(false);

            /* check admin id */

            int adminId = -1;

            try (PreparedStatement checkStmt = conn.prepareStatement(checkAdminQuery)) {

                checkStmt.setInt(1, currentUserId);

                ResultSet rs = checkStmt.executeQuery();

                if (rs.next())
                    adminId = rs.getInt("admin_id");
            }

            /* if user not in admin table */

            if (adminId == -1) {

                System.out.println("User is not registered as admin.");

                conn.rollback();

                return false;
            }

            /* resolve alert */

            try (
                    PreparedStatement updateStmt = conn.prepareStatement(updateAlertQuery);
                    PreparedStatement insertStmt = conn.prepareStatement(insertRemediationQuery)) {

                updateStmt.setInt(1, alertId);

                int rowsUpdated = updateStmt.executeUpdate();

                if (rowsUpdated > 0) {

                    insertStmt.setInt(1, alertId);

                    insertStmt.setInt(2, adminId);

                    insertStmt.setString(3, actionTaken);

                    insertStmt.executeUpdate();

                    conn.commit();

                    return true;
                }

                else {

                    System.out.println("Alert already resolved OR invalid alert ID.");
                }
            }

            catch (SQLException ex) {

                conn.rollback();

                System.out.println("Error processing resolution.");

                ex.printStackTrace();
            }

        }

        catch (SQLException e) {

            e.printStackTrace();
        }

        return false;
    }
}