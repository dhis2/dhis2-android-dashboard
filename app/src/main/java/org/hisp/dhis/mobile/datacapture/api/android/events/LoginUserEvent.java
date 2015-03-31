package org.hisp.dhis.mobile.datacapture.api.android.events;

public final class LoginUserEvent {
    private String mServerUrl;
    private String mUsername;
    private String mPassword;

    public String getServerUrl() {
        return mServerUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.mServerUrl = serverUrl;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }
}
