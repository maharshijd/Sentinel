package dao;

import config.DBconnection;
import model.DeviceDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DeviceDetailsDAO {

    public DeviceDetailsDAO() {
        ensureDeviceDetailsTableExists();
    }

    private void ensureDeviceDetailsTableExists() {
        String query = "CREATE TABLE IF NOT EXISTS device_details ("
                + "device_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "mac_address VARCHAR(32) NOT NULL, "
                + "os_version VARCHAR(100) NOT NULL"
                + ")";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Warning: Could not ensure device_details table exists.");
            e.printStackTrace();
        }
    }

    public int saveDeviceDetails(String macAddress, String osVersion) {
        String query = "INSERT INTO device_details (mac_address, os_version) VALUES (?, ?)";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, macAddress);
            stmt.setString(2, osVersion);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to save device details.");
            e.printStackTrace();
        }

        return -1;
    }

    public DeviceDetails getDeviceById(int deviceId) {
        String query = "SELECT device_id, mac_address, os_version "
                + "FROM device_details WHERE device_id = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, deviceId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new DeviceDetails(
                        rs.getInt("device_id"),
                        rs.getString("mac_address"),
                        rs.getString("os_version"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching device details.");
            e.printStackTrace();
        }

        return null;
    }

    public DeviceDetails getDeviceByMacAddress(String macAddress) {
        String query = "SELECT device_id, mac_address, os_version "
                + "FROM device_details WHERE mac_address = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, macAddress);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new DeviceDetails(
                        rs.getInt("device_id"),
                        rs.getString("mac_address"),
                        rs.getString("os_version"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching device by MAC address.");
            e.printStackTrace();
        }

        return null;
    }

    public void viewAllDevices() {
        String query = "SELECT device_id, mac_address, os_version "
                + "FROM device_details ORDER BY device_id DESC";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n==============================================================");
            System.out.println("                        DEVICE DETAILS                        ");
            System.out.println("==============================================================");
            System.out.printf("%-10s | %-20s | %-20s%n", "DEVICE ID", "MAC ADDRESS", "OS VERSION");
            System.out.println("--------------------------------------------------------------");

            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                System.out.printf("%-10d | %-20s | %-20s%n",
                        rs.getInt("device_id"),
                        rs.getString("mac_address"),
                        rs.getString("os_version"));
            }

            if (!hasRows) {
                System.out.println("No device details found.");
            }
            System.out.println("==============================================================\n");
        } catch (SQLException e) {
            System.out.println("Error viewing device details.");
            e.printStackTrace();
        }
    }
}
