import java.sql.Connection;
import db.DBConnection;

public class TestConnection {
    public static void main(String args[])
    {
        Connection conn = DBConnection.getConnection();
        if (conn != null){
            System.out.println("Database Connected Successfully!!!");
        }
        else{
            System.out.println("Unable to Connect");
        }
    }
}
