package mx.com.broadcastv.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class User implements Serializable {

    private static final long  serialVersionUID = 1L;

    private String userId;

    private String langId;

    private String userName;

    private String userLogon;

    public String getUserId() {
        return userId;
    }

    @JsonProperty("UserId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLangId() {
        return langId;
    }

    @JsonProperty("LangId")
    public void setLangId(String langId) {
        this.langId = langId;
    }

    public String getUserName() {
        return userName;
    }

    @JsonProperty("UserName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserLogon() {
        return userLogon;
    }

    @JsonProperty("UserLogon")
    public void setUserLogon(String userLogon) {
        this.userLogon = userLogon;
    }
}
