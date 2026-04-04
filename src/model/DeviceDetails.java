package model;

public class DeviceDetails {
    private int deviceId;
    private String macAddress;
    private String osVersion;

    public DeviceDetails(int deviceId, String macAddress, String osVersion) {
        this.deviceId = deviceId;
        this.macAddress = macAddress;
        this.osVersion = osVersion;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getOsVersion() {
        return osVersion;
    }
}
