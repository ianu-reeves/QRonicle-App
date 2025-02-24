package com.qronicle.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "password_reset_request")
public class PasswordResetRequest {
    public PasswordResetRequest(String code, User user, Instant expiry) {
        this.code = code;
        this.user = user;
        this.expiry = expiry;
    }

    public PasswordResetRequest() {
    }

    @Id
    @Column(name = "code")
    private String code;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expiry")
    private Instant expiry;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getExpiry() {
        return expiry;
    }

    public void setExpiry(Instant expiry) {
        this.expiry = expiry;
    }

    @Override
    public String toString() {
        return "PasswordResetRequest{" +
                "code='" + code + '\'' +
                ", user=" + user +
                ", expiry=" + expiry +
                '}';
    }
}
