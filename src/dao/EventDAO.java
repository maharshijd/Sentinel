package dao;

import config.DBconnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EventDAO {

    public void viewAllEvents() {

        String query = "SELECT event_id, session_id, event_type, event_score, severity, event_time " +
                "FROM event_logs " +
                "ORDER BY event_time DESC " +
                "LIMIT 20";

        try (
                Connection conn = DBconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n=======================================================================");
            System.out.println("                          RECENT SECURITY EVENTS                       ");
            System.out.println("=======================================================================");

            System.out.printf(
                    "%-8s | %-10s | %-25s | %-6s | %-10s%n",
                    "EVENT ID",
                    "SESSION ID",
                    "EVENT TYPE",
                    "SCORE",
                    "SEVERITY");

            System.out.println("-----------------------------------------------------------------------");

            boolean hasEvents = false;

            while (rs.next()) {

                hasEvents = true;

                System.out.printf(
                        "%-8d | %-10d | %-25s | %-6d | %-10s%n",
                        rs.getInt("event_id"),
                        rs.getInt("session_id"),
                        rs.getString("event_type"),
                        rs.getInt("event_score"),
                        rs.getString("severity"));
            }

            if (!hasEvents) {
                System.out.println("No events logged yet.");
            }

            System.out.println("=======================================================================\n");

        } catch (SQLException e) {

            System.out.println("Error fetching event logs.");

            e.printStackTrace();
        }
    }

    public int logEventAndGetId(
            int sessionId,
            String eventType,
            int eventScore,
            String severity) {

        String query = "INSERT INTO event_logs (session_id, event_type, event_score, severity) " +
                "VALUES (?, ?, ?, ?)";

        try (

                Connection conn = DBconnection.getConnection();

                PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)

        ) {

            stmt.setInt(1, sessionId);
            stmt.setString(2, eventType);
            stmt.setInt(3, eventScore);
            stmt.setString(4, severity);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {

                try (ResultSet keys = stmt.getGeneratedKeys()) {

                    if (keys.next()) {

                        return keys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Failed to log event. Check if Session ID exists.");

            e.printStackTrace();
        }

        return -1;
    }

    public boolean logEvent(
            int sessionId,
            String eventType,
            int eventScore,
            String severity) {

        int eventId = logEventAndGetId(
                sessionId,
                eventType,
                eventScore,
                severity);

        return eventId > 0;
    }
}