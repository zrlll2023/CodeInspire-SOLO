package com.codeinspire.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("prompts")
public class Prompt {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String scene;
    private String content;
    private String variables; // JSON format
    private Integer version = 1;
    private String status = "active"; // active/draft/deprecated
    private Boolean isAbTest = false;
    private String abGroup; // A/B
    private Integer usageCount = 0;
    private BigDecimal satisfactionScore;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
