package dao;

import config.DBconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeviceDAO {

    public DeviceDAO() {
        ensureDeviceTableExists();
    }

    private void ensureDeviceTableExists() {
        String query = "CREATE TABLE IF NOT EXISTS connected_devices ("
                + "device_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "user_id INT NOT NULL, "
                + "device_fingerprint VARCHAR(128) NOT NULL, "
                + "device_name VARCHAR(100) NOT NULL, "
                + "os_name VARCHAR(60), "
                + "ip_address VARCHAR(45), "
                + "status VARCHAR(20) NOT NULL DEFAULT 'CONNECTED', "
                + "first_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                + "UNIQUE KEY uq_user_device (user_id, device_fingerprint), "
                + "CONSTRAINT fk_connected_user FOREIGN KEY (user_id) REFERENCES user(user_id)"
                + ")";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Warning: Could not ensure connected_devices table exists.");
            e.printStackTrace();
        }
    }

    public boolean registerOrUpdateDevice(int userId, String fingerprint, String ipAddress) {
        String updateQuery = "UPDATE connected_devices "
                + "SET ip_address = ?, status = 'CONNECTED', last_seen = CURRENT_TIMESTAMP "
                + "WHERE user_id = ? AND device_fingerprint = ?";

        String insertQuery = "INSERT INTO connected_devices "
                + "(user_id, device_fingerprint, device_name, os_name, ip_address, status) "
                + "VALUES (?, ?, 'N/A', 'N/A', ?, 'CONNECTED')";

        try (Connection conn = DBconnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

                updateStmt.setString(1, ipAddress);
                updateStmt.setInt(2, userId);
                updateStmt.setString(3, fingerprint);

                int rowsUpdated = updateStmt.executeUpdate();

                if (rowsUpdated == 0) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setString(2, fingerprint);
                    insertStmt.setString(3, ipAddress);
                    insertStmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                System.out.println("Failed to register/update device. Transaction rolled back.");
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("Database error while tracking device.");
            e.printStackTrace();
        }

        return false;
    }

    public void viewAllConnectedDevices() {
        String query = "SELECT d.device_id, d.user_id, u.email, d.ip_address, d.status "
                + "FROM connected_devices d "
                + "JOIN user u ON d.user_id = u.user_id "
                + "ORDER BY d.last_seen DESC";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            System.out.println(
                    "\n==================================================================================================");
            System.out.println(
                    "                                      CONNECTED DEVICES                                           ");
            System.out.println(
                    "==================================================================================================");
            System.out.printf("%-10s | %-8s | %-28s | %-16s | %-12s%n",
                    "DEVICE ID", "USER ID", "EMAIL", "IP", "STATUS");
            System.out.println(
                    "--------------------------------------------------------------------------------------------------");

            boolean hasDevices = false;
            while (rs.next()) {
                hasDevices = true;
                System.out.printf("%-10d | %-8d | %-28s | %-16s | %-12s%n",
                        rs.getInt("device_id"),
                        rs.getInt("user_id"),
                        rs.getString("email"),
                        rs.getString("ip_address"),
                        rs.getString("status"));
            }

            if (!hasDevices) {
                System.out.println("No tracked devices found.");
            }
            System.out.println(
                    "==================================================================================================\n");

        } catch (SQLException e) {
            System.out.println("Error fetching connected devices.");
            e.printStackTrace();
        }
    }

    public void viewConnectedDevicesByUser(int userId) {
        String query = "SELECT device_id, ip_address, status "
                + "FROM connected_devices "
                + "WHERE user_id = ? "
                + "ORDER BY last_seen DESC";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n====================================================================");
            System.out.println("                        YOUR CONNECTED DEVICES                      ");
            System.out.println("====================================================================");
            System.out.printf("%-10s | %-16s | %-12s%n", "DEVICE ID", "IP", "STATUS");
            System.out.println("--------------------------------------------------------------------");

            boolean hasDevices = false;
            while (rs.next()) {
                hasDevices = true;
                System.out.printf("%-10d | %-16s | %-12s%n",
                        rs.getInt("device_id"),
                        rs.getString("ip_address"),
                        rs.getString("status"));
            }

            if (!hasDevices) {
                System.out.println("No devices found for your account.");
            }
            System.out.println("====================================================================\n");

        } catch (SQLException e) {
            System.out.println("Error fetching your devices.");
            e.printStackTrace();
        }
    }

    public boolean disconnectDeviceAsAdmin(int deviceId) {
        String query = "UPDATE connected_devices SET status = 'DISCONNECTED' WHERE device_id = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, deviceId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to disconnect device.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean disconnectOwnDevice(int deviceId, int userId) {
        String query = "UPDATE connected_devices SET status = 'DISCONNECTED' WHERE device_id = ? AND user_id = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, deviceId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Failed to disconnect your device.");
            e.printStackTrace();
            return false;
        }
    }
}
