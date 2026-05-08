package com.codeinspire.service;

import com.codeinspire.dto.NotificationVO;
import java.util.List;

public interface NotificationService {
    NotificationVO createNotification(Long userId, String type, String title, String content);
    List<NotificationVO> getUserNotifications(Long userId, Boolean isRead);
    NotificationVO markAsRead(Long userId, Long notificationId);
    void markAllAsRead(Long userId);
    long getUnreadCount(Long userId);
    void deleteNotification(Long userId, Long notificationId);
}
