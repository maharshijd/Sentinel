package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection getConnection(){
        Connection conn = null;
        try{
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/sentinel_db","root","root");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return conn;
    }
    
}
