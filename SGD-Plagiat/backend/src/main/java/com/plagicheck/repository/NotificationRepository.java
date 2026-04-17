package com.plagicheck.repository;

import com.plagicheck.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndLuOrderByCreatedAtDesc(Long userId, Boolean lu);
    long countByUserIdAndLu(Long userId, Boolean lu);
}
