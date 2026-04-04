package model;

import java.sql.Timestamp;

public class SessionDetails {
    private int sessionId;
    private int userId;
    private String ipAddress;
    private Timestamp startTime;

    public SessionDetails(int sessionId, int userId, String ipAddress, Timestamp startTime) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.startTime = startTime;
    }

    public int getSessionId() {
        return sessionId;
    }

    public int getUserId() {
        return userId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Timestamp getStartTime() {
        return startTime;
    }
}
