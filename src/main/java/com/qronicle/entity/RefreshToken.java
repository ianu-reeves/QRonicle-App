package com.qronicle.entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity(name = "refresh_token")
public class RefreshToken {
    @Id
    @Column(name = "token_value")
    private String tokenValue;

    @Column(name = "expiry")
    private Instant expiry;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User sub;

    @Column(name = "user_agent")
    private String userAgent;

    public RefreshToken() {
    }

    public RefreshToken(String tokenValue, Instant expiry, User sub, String userAgent) {
        this.tokenValue = tokenValue;
        this.expiry = expiry;
        this.sub = sub;
        this.userAgent = userAgent;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Instant getExpiry() {
        return expiry;
    }

    public void setExpiry(Instant expiry) {
        this.expiry = expiry;
    }

    public User getSub() {
        return sub;
    }

    public void setSub(User sub) {
        this.sub = sub;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(tokenValue, that.tokenValue) && Objects.equals(sub, that.sub) && Objects.equals(userAgent, that.userAgent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenValue, sub, userAgent);
    }
}
