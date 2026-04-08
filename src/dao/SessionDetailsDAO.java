package dao;

import config.DBconnection;
import model.SessionDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SessionDetailsDAO {

    public SessionDetailsDAO() {
        ensureSessionDetailsTableExists();
    }

    private void ensureSessionDetailsTableExists() {
        String query = "CREATE TABLE IF NOT EXISTS session_details ("
                + "session_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "user_id INT NOT NULL, "
                + "ip_address VARCHAR(45) NOT NULL, "
                + "start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "CONSTRAINT fk_session_details_user FOREIGN KEY (user_id) REFERENCES user(user_id)"
                + ")";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Warning: Could not ensure session_details table exists.");
            e.printStackTrace();
        }
    }

    public int createSession(int userId, String ipAddress) {
        String query = "INSERT INTO session_details (user_id, ip_address) VALUES (?, ?)";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.setString(2, ipAddress);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to create session details.");
            e.printStackTrace();
        }

        return -1;
    }

    public SessionDetails getSessionById(int sessionId) {
        String query = "SELECT session_id, user_id, ip_address, start_time "
                + "FROM session_details WHERE session_id = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new SessionDetails(
                        rs.getInt("session_id"),
                        rs.getInt("user_id"),
                        rs.getString("ip_address"),
                        rs.getTimestamp("start_time"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching session details.");
            e.printStackTrace();
        }

        return null;
    }

    public int getLatestSessionId(int userId) {

        String query = "SELECT session_id FROM session_details WHERE user_id=? ORDER BY start_time DESC LIMIT 1";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next())
                return rs.getInt("session_id");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void viewSessionsByUser(int userId) {
        String query = "SELECT session_id, ip_address, start_time "
                + "FROM session_details WHERE user_id = ? ORDER BY start_time DESC";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n================================================================================");
            System.out.println("                                SESSION DETAILS                                 ");
            System.out.println("================================================================================");
            System.out.printf("%-12s | %-16s | %-20s%n",
                    "SESSION ID", "IP ADDRESS", "START TIME");
            System.out.println("--------------------------------------------------------------------------------");

            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;

                System.out.printf("%-12d | %-16s | %-20s%n",
                        rs.getInt("session_id"),
                        rs.getString("ip_address"),
                        rs.getTimestamp("start_time"));
            }

            if (!hasRows) {
                System.out.println("No session details found for this user.");
            }
            System.out.println("================================================================================\n");
        } catch (SQLException e) {
            System.out.println("Error viewing session details.");
            e.printStackTrace();
        }
    }
}
