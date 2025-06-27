package com.tact.readwe.announcement.dto;

public record CreateAnnouncementRequest(
        String title,
        String content,
        String link,
        String linkText
) {
}
