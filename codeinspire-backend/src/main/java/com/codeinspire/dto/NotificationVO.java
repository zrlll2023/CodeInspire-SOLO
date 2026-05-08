package com.codeinspire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationVO {
    private Long id;
    private String type;
    private String title;
    private String content;
    private String data; // JSON string
    private Boolean isRead;
    private LocalDateTime readAt;
    private String channel;
    private LocalDateTime createdAt;
}
