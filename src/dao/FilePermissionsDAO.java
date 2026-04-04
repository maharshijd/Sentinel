package dao;
import config.DBconnection;
import java.sql.*;

public class FilePermissionsDAO {
    public boolean isallowed(int userID, String filename, String action){
        boolean allowed= false;
        try{
            Connection conn = DBconnection.getConnection();

            String query= "select allowed from file_permissions where user_id=? and filename=? and action=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userID);
            ps.setString(2, filename);
            ps.setString(3, action);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                allowed = rs.getBoolean("allowed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allowed;
    }

}