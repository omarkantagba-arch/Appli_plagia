package com.plagicheck.service;

import com.plagicheck.entity.Notification;
import com.plagicheck.entity.User;
import com.plagicheck.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void createNotification(User user, String titre, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType("SOUTENANCE");
        notification.setTitre(titre);
        notification.setMessage(message);
        notification.setPriority(Notification.Priority.MEDIUM);
        notification.setLu(false);
        notificationRepository.save(notification);
    }
}
