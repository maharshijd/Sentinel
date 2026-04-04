package model;

import java.sql.Timestamp;

public class ConnectedDevice {
    private int deviceId;
    private int userId;
    private String deviceFingerprint;
    private String deviceName;
    private String osName;
    private String ipAddress;
    private String status;
    private Timestamp firstSeen;
    private Timestamp lastSeen;

    public ConnectedDevice(int deviceId, int userId, String deviceFingerprint, String deviceName,
            String osName, String ipAddress, String status, Timestamp firstSeen, Timestamp lastSeen) {
        this.deviceId = deviceId;
        this.userId = userId;
        this.deviceFingerprint = deviceFingerprint;
        this.deviceName = deviceName;
        this.osName = osName;
        this.ipAddress = ipAddress;
        this.status = status;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public int getUserId() {
        return userId;
    }

    public String getDeviceFingerprint() {
        return deviceFingerprint;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getOsName() {
        return osName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getFirstSeen() {
        return firstSeen;
    }

    public Timestamp getLastSeen() {
        return lastSeen;
    }
}
