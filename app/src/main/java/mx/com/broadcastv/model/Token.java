package mx.com.broadcastv.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Token implements Serializable {

    private static final long  serialVersionUID = 1L;

    private String DeviceId;

    private String DeviceOSName;

    private String DeviceOSVersion;

    public String getDeviceId() {
        return DeviceId;
    }

    /*
    * Adding hack JsonProperty to change allow uppercase in the request
    * */
    @JsonProperty("DeviceId")
    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getDeviceOSName() {
        return DeviceOSName;
    }

    @JsonProperty("DeviceOSName")
    public void setDeviceOSName(String deviceOSName) {
        DeviceOSName = deviceOSName;
    }

    public String getDeviceOSVersion() {
        return DeviceOSVersion;
    }

    @JsonProperty("DeviceOSVersion")
    public void setDeviceOSVersion(String deviceOSVersion) {
        DeviceOSVersion = deviceOSVersion;
    }
}
