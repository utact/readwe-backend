package com.tact.readwe.announcement.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table
@EntityListeners(AuditingEntityListener.class)
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private String link;

    @Column
    private String linkText;

    @Column(nullable = false)
    private boolean isActive;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    protected Announcement() {}

    private Announcement(String title, String content, String link, String linkText, boolean isActive) {
        this.title = title;
        this.content = content;
        this.link = link;
        this.linkText = linkText;
        this.isActive = isActive;
    }

    public static Announcement create(String title, String content) {
        return new Announcement(title, content, "", "", true);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
