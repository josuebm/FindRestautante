package com.example.josu.findrestautant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by stage on 9/04/15.
 */
public class WebFields {

    private boolean logged, enableScanner, enableCheckIn;
    private String expireSessionDateTime, expireSessionTimeStamp, sessionToken, errorMessages;

    public WebFields() {
    }

    public WebFields(boolean logged, boolean enableScanner, boolean enableCheckIn, String expireSessionDateTime, String expireSessionTimeStamp, String sessionToken, String errorMessages) {
        this.logged = logged;
        this.enableScanner = enableScanner;
        this.enableCheckIn = enableCheckIn;
        this.expireSessionDateTime = expireSessionDateTime;
        this.expireSessionTimeStamp = expireSessionTimeStamp;
        this.sessionToken = sessionToken;
        this.errorMessages = errorMessages;
    }

    public WebFields(JSONObject object) throws JSONException {
        this.logged = object.getBoolean("logged");
        this.enableScanner = object.getBoolean("enableScanner");
        this.enableCheckIn = object.getBoolean("enableCheckIn");
        this.expireSessionDateTime = object.getString("expireSessionDateTime");
        this.expireSessionTimeStamp = object.getString("expireSessionTimeStamp");
        this.sessionToken = object.getString("sessionToken");
        this.errorMessages = object.getString("errorMessages");
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public String getExpireSessionDateTime() {
        return expireSessionDateTime;
    }

    public void setExpireSessionDateTime(String expireSessionDateTime) {
        this.expireSessionDateTime = expireSessionDateTime;
    }

    public String getExpireSessionTimeStamp() {
        return expireSessionTimeStamp;
    }

    public void setExpireSessionTimeStamp(String expireSessionTimeStamp) {
        this.expireSessionTimeStamp = expireSessionTimeStamp;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(String errorMessages) {
        this.errorMessages = errorMessages;
    }

    public boolean isEnableScanner() {
        return enableScanner;
    }

    public void setEnableScanner(boolean enableScanner) {
        this.enableScanner = enableScanner;
    }

    public boolean isEnableCheckIn() {
        return enableCheckIn;
    }

    public void setEnableCheckIn(boolean enableCheckIn) {
        this.enableCheckIn = enableCheckIn;
    }

    @Override
    public String toString() {
        return "WebFields{" +
                "logged=" + logged +
                ", enableScanner=" + enableScanner +
                ", enableCheckIn=" + enableCheckIn +
                ", expireSessionDateTime='" + expireSessionDateTime + '\'' +
                ", expireSessionTimeStamp='" + expireSessionTimeStamp + '\'' +
                ", sessionToken='" + sessionToken + '\'' +
                ", errorMessages='" + errorMessages + '\'' +
                '}';
    }
}
