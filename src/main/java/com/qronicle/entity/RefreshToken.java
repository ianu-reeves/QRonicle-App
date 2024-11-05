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

    public RefreshToken() {
    }

    public RefreshToken(String tokenValue, Instant expiry, User sub) {
        this.tokenValue = tokenValue;
        this.expiry = expiry;
        this.sub = sub;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(tokenValue, that.tokenValue) && Objects.equals(expiry, that.expiry) && Objects.equals(sub, that.sub);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenValue, expiry, sub);
    }
}
