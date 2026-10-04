package com.backend.crosswords.admin.models;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "_verifycode")
public class VerifyCode {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "code")
    private Integer code;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public VerifyCode() {
    }

    public VerifyCode(Integer code, User user) {
        this.code = code;
        this.user = user;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getUserId() {
        return userId;
    }
}
