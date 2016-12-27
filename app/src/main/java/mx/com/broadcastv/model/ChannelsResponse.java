package mx.com.broadcastv.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONObject;

/**
 * Created by omarin on 12/11/16.
 */

public class ChannelsResponse{

    private String ChannelId;

    private String UserId;

    private String GroupId;

    private String GroupName;

    private String ChannelName;

    private String ChannelText;

    private String ChannelURL;

    private String ChannelLogo;

    private String ChannelLanguage;

    public String getChannelId() {
        return ChannelId;
    }

    @JsonProperty("ChannelId")
    public void setChannelId(String channelId) {
        ChannelId = channelId;
    }

    public String getUserId() {
        return UserId;
    }

    @JsonProperty("UserId")
    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getGroupId() {
        return GroupId;
    }

    @JsonProperty("GroupId")
    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getGroupName() {
        return GroupName;
    }

    @JsonProperty("GroupName")
    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getChannelName() {
        return ChannelName;
    }

    @JsonProperty("ChannelName")
    public void setChannelName(String channelName) {
        ChannelName = channelName;
    }

    public String getChannelText() {
        return ChannelText;
    }

    @JsonProperty("ChannelText")
    public void setChannelText(String channelText) {
        ChannelText = channelText;
    }

    public String getChannelURL() {
        return ChannelURL;
    }

    @JsonProperty("ChannelURL")
    public void setChannelURL(String channelURL) {
        ChannelURL = channelURL;
    }

    public String getChannelLogo() {
        return ChannelLogo;
    }

    @JsonProperty("ChannelLogo")
    public void setChannelLogo(String channelLogo) {
        ChannelLogo = channelLogo;
    }

    public String getChannelLanguage() {
        return ChannelLanguage;
    }

    @JsonProperty("ChannelLanguage")
    public void setChannelLanguage(String channelLanguage) {
        ChannelLanguage = channelLanguage;
    }
}
