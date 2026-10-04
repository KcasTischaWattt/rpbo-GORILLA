package com.backend.crosswords.admin.models;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "_refresh")
public class RefreshToken {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "ip")
    private String ip;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "expiry_date")
    private Instant expiryDate;

    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "new_refresh_id")
    private RefreshToken newRefresh;

    public RefreshToken(String token, String ip, String userAgent, User user, Instant expiryDate) {
        this.token = token;
        this.ip = ip;
        this.userAgent = userAgent;
        this.user = user;
        this.expiryDate = expiryDate;
        this.newRefresh = null;
    }

    public RefreshToken() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public RefreshToken getNewRefresh() {
        return newRefresh;
    }

    public void setNewRefresh(RefreshToken newRefresh) {
        this.newRefresh = newRefresh;
    }
}
