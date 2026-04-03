package dao;

import config.DBconnection;
import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User authenticate(String email, String passwordHash) {
        String query = "SELECT u.user_id, u.email, u.role_id, u.dept_id, r.role_name " +
                "FROM user u JOIN role r ON u.role_id = r.role_id " +
                "WHERE u.email = ? AND u.password_hash = ?";

        try (Connection conn = DBconnection.getConnection();
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
            System.out.println("Database error during login.");
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(String email, String passwordHash, int roleId, int deptId) {
        String query = "INSERT INTO user (email, password_hash, role_id, dept_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, passwordHash);
            stmt.setInt(3, roleId);
            stmt.setInt(4, deptId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error: Email might already exist or invalid Role/Dept IDs.");
        }
        return false;
    }
}