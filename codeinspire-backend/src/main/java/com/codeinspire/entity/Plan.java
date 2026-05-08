package com.codeinspire.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("plans")
public class Plan {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String title;
    private String description;
    private String status; // active/completed/paused/archived
    private LocalDate startDate;
    private LocalDate endDate;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
