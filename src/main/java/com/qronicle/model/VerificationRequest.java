package com.qronicle.model;

public class VerificationRequest {
    private String tokenString;
    private String username;

    public VerificationRequest() {
    }

    public VerificationRequest(String tokenString, String username) {
        this.tokenString = tokenString;
        this.username = username;
    }

    public String getVerificationToken() {
        return tokenString;
    }

    public void setVerificationToken(String tokenString) {
        this.tokenString = tokenString;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "VerificationRequest{" +
                "tokenString='" + tokenString + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
