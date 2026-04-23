package dao;

import config.DBconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;

public class UserDAO {

    public User authenticate(String email, String passwordHash) {

        String query = "SELECT u.user_id, u.email, u.role_id, u.dept_id, r.role_name " +
                "FROM user u JOIN role r ON u.role_id = r.role_id " +
                "WHERE u.email = ? AND u.password_hash = ?";

        try (
                Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, passwordHash);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                return new User(

                        rs.getInt("user_id"),
                        rs.getString("email"),
                        rs.getInt("role_id"),
                        rs.getInt("dept_id"),
                        rs.getString("role_name"));
            }
        } catch (SQLException e) {

            System.out.println("Database error during login");

            e.printStackTrace();
        }

        return null;
    }

    public boolean registerUser(
            String email,
            String passwordHash,
            int roleId,
            int deptId) {

        String query = "INSERT INTO user(email,password_hash,role_id,dept_id) VALUES(?,?,?,?)";

        try (
                Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, passwordHash);
            stmt.setInt(3, roleId);
            stmt.setInt(4, deptId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {

            System.out.println(
                    "Registration failed. Email may already exist.");
        }

        return false;
    }

    public void viewAllUsers() {

        String query = "SELECT user_id,email,role_id,dept_id FROM user";

        try (
                Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n==================== USERS ====================");

            System.out.printf(
                    "%-6s %-25s %-6s %-6s%n",
                    "ID",
                    "EMAIL",
                    "ROLE",
                    "DEPT");

            while (rs.next()) {

                System.out.printf(
                        "%-6d %-25s %-6d %-6d%n",

                        rs.getInt("user_id"),
                        rs.getString("email"),
                        rs.getInt("role_id"),
                        rs.getInt("dept_id"));
            }

            System.out.println("===============================================\n");

        } catch (SQLException e) {

            System.out.println("Error fetching users");

            e.printStackTrace();
        }
    }

    public boolean deleteUser(int userId) {

        Connection conn = null;
        try {
            conn = DBconnection.getConnection();
            conn.setAutoCommit(false);
            String deleteSessions = "DELETE FROM session_details WHERE user_id=?";
            String deleteDevices = "DELETE FROM connected_devices WHERE user_id=?";
            String deleteDeviceDetails = "DELETE FROM device_details WHERE device_id IN " + "(SELECT device_id FROM connected_devices WHERE user_id=?)";
            String deleteAdmin = "DELETE FROM admin WHERE user_id=?";
            
            PreparedStatement s1 = conn.prepareStatement(deleteSessions);
            s1.setInt(1, userId);
            s1.executeUpdate();
            
            PreparedStatement s2 = conn.prepareStatement(deleteDevices);
            s2.setInt(1, userId);
            s2.executeUpdate();
            
            PreparedStatement s3 = conn.prepareStatement(deleteDeviceDetails);
            s3.setInt(1, userId);
            s3.executeUpdate();

            PreparedStatement s4 = conn.prepareStatement(deleteAdmin);
            s4.setInt(1, userId);
            s4.executeUpdate();
            
            String deleteUserQuery = "DELETE FROM user WHERE user_id=?";
            PreparedStatement s5 = conn.prepareStatement(deleteUserQuery);
            s5.setInt(1, userId);
            int rows = s5.executeUpdate();
            
            conn.commit();
            return rows > 0;
        } catch (Exception e) {
            try {
                if (conn != null)
                    conn.rollback();
            } 
            catch (Exception ex) {
            }
            System.out.println("Error deleting user");
            e.printStackTrace();
        }

        return false;
    }
}