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

            System.out.println("\n========== USERS ==========");

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

            System.out.println("===========================\n");

        } catch (SQLException e) {

            System.out.println("Error fetching users");

            e.printStackTrace();
        }
    }

public boolean deleteUser(int userId){

Connection conn=null;

try{

conn=DBconnection.getConnection();

conn.setAutoCommit(false);

/* get session ids */

String getSessions=
"SELECT session_id FROM session_details WHERE user_id=?";

PreparedStatement ps0=conn.prepareStatement(getSessions);

ps0.setInt(1,userId);

ResultSet rs=ps0.executeQuery();



while(rs.next()){

int sessionId=rs.getInt("session_id");

/* delete events */

PreparedStatement ps1=
conn.prepareStatement(
"DELETE FROM event_logs WHERE session_id=?"
);

ps1.setInt(1,sessionId);

ps1.executeUpdate();

}



/* delete sessions */

PreparedStatement ps2=
conn.prepareStatement(
"DELETE FROM session_details WHERE user_id=?"
);

ps2.setInt(1,userId);

ps2.executeUpdate();



/* delete devices */

PreparedStatement ps3=
conn.prepareStatement(
"DELETE FROM connected_devices WHERE user_id=?"
);

ps3.setInt(1,userId);

ps3.executeUpdate();



/* delete admin record */

PreparedStatement ps4=
conn.prepareStatement(
"DELETE FROM admin WHERE user_id=?"
);

ps4.setInt(1,userId);

ps4.executeUpdate();



/* finally delete user */

PreparedStatement ps5=
conn.prepareStatement(
"DELETE FROM user WHERE user_id=?"
);

ps5.setInt(1,userId);

int rows=ps5.executeUpdate();



conn.commit();

return rows>0;

}
catch(Exception e){

try{

if(conn!=null)

conn.rollback();

}
catch(Exception ex){}

System.out.println("Delete failed due to related records");

e.printStackTrace();
}

return false;
}

    return false;
}}