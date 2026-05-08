package com.codeinspire.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.codeinspire.dto.NotificationVO;
import com.codeinspire.entity.Notification;
import com.codeinspire.repository.NotificationMapper;
import com.codeinspire.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public NotificationVO createNotification(Long userId, String type, String title, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setChannel("in_app");
        notification.setSendStatus("pending");
        
        notificationMapper.insert(notification);
        
        log.info("Created notification for user {}: {}", userId, title);
        
        sendWebSocketNotification(userId, convertToVO(notification));
        
        return convertToVO(notification);
    }

    @Override
    public List<NotificationVO> getUserNotifications(Long userId, Boolean isRead) {
        LambdaQueryWrapper<Notification> query = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreatedAt);
        
        if (isRead != null) {
            query.eq(Notification::getIsRead, isRead);
        }
        
        List<Notification> notifications = notificationMapper.selectList(query);
        
        return notifications.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationVO markAsRead(Long userId, Long notificationId) {
        Notification notification = validateOwnership(userId, notificationId);
        
        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationMapper.updateById(notification);
        }
        
        return convertToVO(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        Notification update = new Notification();
        update.setIsRead(true);
        update.setReadAt(LocalDateTime.now());
        
        notificationMapper.update(update,
                new LambdaUpdateWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, false)
        );
        
        log.info("Marked all notifications as read for user: {}", userId);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationMapper.selectCount(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, false)
        );
    }

    @Override
    public void deleteNotification(Long userId, Long notificationId) {
        validateOwnership(userId, notificationId);
        notificationMapper.deleteById(notificationId);
    }

    private void sendWebSocketNotification(Long userId, NotificationVO notification) {
        try {
            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + userId,
                    notification
            );
            log.debug("Sent WebSocket notification to user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification: {}", e.getMessage());
        }
    }

    private Notification validateOwnership(Long userId, Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        
        if (notification == null) {
            throw new RuntimeException("通知不存在");
        }
        
        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问此通知");
        }
        
        return notification;
    }

    private NotificationVO convertToVO(Notification notification) {
        return NotificationVO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .content(notification.getContent())
                .data(notification.getData())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .channel(notification.getChannel())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
