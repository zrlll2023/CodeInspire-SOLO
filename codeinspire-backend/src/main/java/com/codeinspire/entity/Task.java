package com.codeinspire.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("tasks")
public class Task {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long planId;
    private String title;
    private String description;
    private String status; // pending/in_progress/completed/paused/cancelled/overdue
    private Integer priority; // 0-5
    private LocalDate dueDate;
    private LocalDateTime completedAt;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
