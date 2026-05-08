package com.codeinspire.controller;

import com.codeinspire.common.Result;
import com.codeinspire.dto.NotificationVO;
import com.codeinspire.security.JwtTokenProvider;
import com.codeinspire.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public Result<List<NotificationVO>> getNotifications(
            @RequestParam(required = false) Boolean isRead,
            HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        List<NotificationVO> notifications = notificationService.getUserNotifications(userId, isRead);
        return Result.success(notifications);
    }

    @GetMapping("/unread-count")
    public Result<Map<String, Long>> getUnreadCount(HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        long count = notificationService.getUnreadCount(userId);
        return Result.success(Map.of("count", count));
    }

    @PutMapping("/{notificationId}/read")
    public Result<NotificationVO> markAsRead(@PathVariable Long notificationId,
                                             HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        NotificationVO notification = notificationService.markAsRead(userId, notificationId);
        return Result.success(notification);
    }

    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        notificationService.markAllAsRead(userId);
        return Result.success();
    }

    @DeleteMapping("/{notificationId}")
    public Result<Void> deleteNotification(@PathVariable Long notificationId,
                                           HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        notificationService.deleteNotification(userId, notificationId);
        return Result.success();
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String token = extractToken(request);
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
