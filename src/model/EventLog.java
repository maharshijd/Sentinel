package model;

import java.sql.Timestamp;

public class EventLog {
    private int eventId;
    private int sessionId;
    private String eventType;
    private int eventScore;
    private String severity;
    private Timestamp eventTime;

    public EventLog(int eventId, int sessionId, String eventType, int eventScore, String severity,
            Timestamp eventTime) {
        this.eventId = eventId;
        this.sessionId = sessionId;
        this.eventType = eventType;
        this.eventScore = eventScore;
        this.severity = severity;
        this.eventTime = eventTime;
    }

    public int getEventId() {
        return eventId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public String getEventType() {
        return eventType;
    }

    public int getEventScore() {
        return eventScore;
    }

    public String getSeverity() {
        return severity;
    }

    public Timestamp getEventTime() {
        return eventTime;
    }
}