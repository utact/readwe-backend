package com.tact.readwe.announcement.controller;

import com.tact.readwe.announcement.dto.CreateAnnouncementRequest;
import com.tact.readwe.announcement.service.AnnouncementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {
    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody CreateAnnouncementRequest request) {
        announcementService.createAnnouncement(request);
        return ResponseEntity.ok().build();
    }
}
