package mx.com.broadcastv.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Channels implements Serializable {

    private static final long serialVersionUID = 1L;

    private String DeviceToken;

    public String getDeviceToken() {
        return DeviceToken;
    }

    @JsonProperty("DeviceToken")
    public void setDeviceToken(String deviceToken) {
        DeviceToken = deviceToken;
    }
}
