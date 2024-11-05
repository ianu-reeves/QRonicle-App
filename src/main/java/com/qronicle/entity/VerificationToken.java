package com.qronicle.entity;

import javax.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "verification_token")
public class VerificationToken {
    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public VerificationToken() {
    }

    @Id
    @Column(name = "token")
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expiry")
    private Instant expiry = Instant.now().plus(5, ChronoUnit.MINUTES);

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
}
