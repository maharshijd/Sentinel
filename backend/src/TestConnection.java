import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import db.DBConnection;

public class TestConnection {

    public static void main(String[] args) {

        String email = "admin@sentinel.com";
        String password = "test123";

        try {

            Connection conn = DBConnection.getConnection();

            String sql = "SELECT * FROM user WHERE email=? AND password_hash=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                System.out.println("Login successful");
                System.out.println("User ID: " + rs.getInt("user_id"));

            } else {

                System.out.println("Invalid credentials");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
