package com.tact.readwe.user.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
public class User {
    @Id
    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "color_code", length = 7)
    private String colorCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public User() {}

    private User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static User signUpOf(String name, String email, String password) {
        return new User(name, email, password);
    }

    @PrePersist
    protected void onCreate() {
        this.userId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
