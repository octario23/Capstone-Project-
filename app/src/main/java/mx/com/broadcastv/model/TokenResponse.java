package mx.com.broadcastv.model;

import java.io.Serializable;

public class TokenResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String Code;

    private String Message;

    private String Success;

    private String Token;

    private User User;

    private Channels Channels;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getSuccess() {
        return Success;
    }

    public void setSuccess(String success) {
        Success = success;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public mx.com.broadcastv.model.User getUser() {
        return User;
    }

    public void setUser(mx.com.broadcastv.model.User user) {
        User = user;
    }

    public mx.com.broadcastv.model.Channels getChannels() {
        return Channels;
    }

    public void setChannels(mx.com.broadcastv.model.Channels channels) {
        Channels = channels;
    }
}