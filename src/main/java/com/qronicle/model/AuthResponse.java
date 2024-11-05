package com.qronicle.model;

import com.qronicle.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

// Class for representing the authentication response.
// Contains the user's details & sign-in time
public class AuthResponse {
    private Collection<? extends GrantedAuthority> roles;
    private User userDetails;
    private long signInTime;

    public AuthResponse() {
    }

    public AuthResponse(User user, long signInTime) {
        this.userDetails = user;
        this.signInTime = signInTime;
    }

    public User getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(User user) {
        this.userDetails = user;
    }

    public long getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(long signInTime) {
        this.signInTime = signInTime;
    }
}
