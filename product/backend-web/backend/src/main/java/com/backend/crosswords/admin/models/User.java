package com.backend.crosswords.admin.models;

import com.backend.crosswords.admin.enums.RoleEnum;
import com.backend.crosswords.corpus.models.DocRating;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "_users")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "send_to_mail")
    private Boolean sendToMail;

    @Column(name = "mobile_notifications")
    private Boolean mobileNotifications;

    @Column(name = "personal_send_to_mail")
    private Boolean personalSendToMail;

    @Column(name = "personal_mobile_notifications")
    private Boolean personalMobileNotifications;

    @Column(name = "subscribable")
    private Boolean subscribable;

    @Column(name = "verified")
    private Boolean verified = false;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(name = "telegram_notifications")
    private Boolean telegramNotifications = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocRating> ratings;

    public User(Long id, String name, String surname, String username, String email, String password, List<DocRating> ratings) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.email = email;
        this.password = password;
        this.ratings = ratings;

        this.sendToMail = false;
        this.mobileNotifications = false;
        this.personalSendToMail = false;
        this.personalMobileNotifications = false;
        this.subscribable = true;
        this.telegramNotifications = false;
    }

    public User(Long id, String name, String surname, String username, String password, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.email = email;
        this.ratings = new ArrayList<>();

        this.sendToMail = false;
        this.mobileNotifications = false;
        this.personalSendToMail = false;
        this.personalMobileNotifications = false;
        this.subscribable = true;
        this.telegramNotifications = false;
    }

    public User() {
    }

    public User(String name, String surname, String email, String password, RoleEnum role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.username = email.split("@")[0];
        this.password = password;
        this.role = role;

        this.sendToMail = false;
        this.mobileNotifications = false;
        this.personalSendToMail = false;
        this.personalMobileNotifications = false;
        this.subscribable = true;
        this.telegramNotifications = false;
    }

    public List<DocRating> getRatings() {
        return ratings;
    }

    public void setRatings(List<DocRating> ratings) {
        this.ratings = ratings;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public Boolean getSendToMail() {
        return sendToMail;
    }

    public void setSendToMail(Boolean sendToMail) {
        this.sendToMail = sendToMail;
    }

    public Boolean getMobileNotifications() {
        return mobileNotifications;
    }

    public void setMobileNotifications(Boolean mobileNotifications) {
        this.mobileNotifications = mobileNotifications;
    }

    public Boolean getPersonalSendToMail() {
        return personalSendToMail;
    }

    public void setPersonalSendToMail(Boolean personalSendToMail) {
        this.personalSendToMail = personalSendToMail;
    }

    public Boolean getPersonalMobileNotifications() {
        return personalMobileNotifications;
    }

    public void setPersonalMobileNotifications(Boolean personalMobileNotifications) {
        this.personalMobileNotifications = personalMobileNotifications;
    }

    public Boolean getSubscribable() {
        return subscribable;
    }

    public void setSubscribable(Boolean subscribable) {
        this.subscribable = subscribable;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public Boolean getTelegramNotifications() {
        return telegramNotifications;
    }

    public void setTelegramNotifications(Boolean telegramNotifications) {
        this.telegramNotifications = telegramNotifications;
    }
}
