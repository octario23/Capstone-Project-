package mx.com.broadcastv.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AddChannelList {

    private List<AddChannel> Channels;

    private String Token;

    public List<AddChannel> getChannelList() {
        return Channels;
    }

    @JsonProperty("Channels")
    public void setChannelList(List<AddChannel> channelList) {
        Channels = channelList;
    }

    public String getToken() {
        return Token;
    }

    @JsonProperty("Token")
    public void setToken(String token) {
        Token = token;
    }
}
