package com.codeinspire.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName("notifications")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String type; // task_reminder/time_node/progress_warning/ai_reply/system
    private String title;
    private String content;
    private String data; // JSON format for related data
    private Boolean isRead = false;
    private LocalDateTime readAt;
    private String channel; // in_app/email/websocket
    private String sendStatus; // pending/sent/failed
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
