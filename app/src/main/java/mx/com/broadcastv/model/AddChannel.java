package mx.com.broadcastv.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddChannel {

    private String ChannelName;

    private String ChannelURL;

    public String getChannelName() {
        return ChannelName;
    }
    @JsonProperty("ChannelName")
    public void setChannelName(String channelName) {
        ChannelName = channelName;
    }

    public String getChannelURL() {
        return ChannelURL;
    }

    @JsonProperty("ChannelURL")
    public void setChannelURL(String channelURL) {
        ChannelURL = channelURL;
    }
}
