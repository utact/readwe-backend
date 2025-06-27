package com.tact.readwe.announcement.service;

import com.tact.readwe.announcement.dto.CreateAnnouncementRequest;
import com.tact.readwe.announcement.entity.Announcement;
import com.tact.readwe.announcement.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    public void createAnnouncement(CreateAnnouncementRequest request) {
        announcementRepository.save(Announcement.create(request.title(), request.content()));
    }
}
