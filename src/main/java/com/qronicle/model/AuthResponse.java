package com.qronicle.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

// Class for representing the authentication response.
// Contains the JWT created using the user's credentials
public class AuthResponse {
    private String username;
    private Collection<? extends GrantedAuthority> roles;
    private long timestamp;

    public AuthResponse() {
    }

    public AuthResponse(String username, Collection<? extends GrantedAuthority> roles, long timestamp) {
        this.username = username;
        this.roles = roles;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Collection<? extends GrantedAuthority> getRoles() {
        return roles;
    }

    public void setRoles(Collection<? extends GrantedAuthority> roles) {
        this.roles = roles;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
