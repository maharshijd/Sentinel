package model;

import java.sql.Timestamp;

public class Alert {
    private int alertId;
    private int eventId;
    private String alertType;
    private String securityLevel;
    private String alertStatus;
    private Timestamp createdTime;

    public Alert(int alertId, int eventId, String alertType, String securityLevel, String alertStatus,
            Timestamp createdTime) {
        this.alertId = alertId;
        this.eventId = eventId;
        this.alertType = alertType;
        this.securityLevel = securityLevel;
        this.alertStatus = alertStatus;
        this.createdTime = createdTime;
    }

    public int getAlertId() {
        return alertId;
    }

    public int getEventId() {
        return eventId;
    }

    public String getAlertType() {
        return alertType;
    }

    public String getSecurityLevel() {
        return securityLevel;
    }

    public String getAlertStatus() {
        return alertStatus;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }
}