package com.codeinspire.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ai_call_logs")
public class AiCallLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String provider;
    private String model;
    private Long promptTemplateId;
    private Integer promptVersion;
    private Integer inputTokens;
    private Integer outputTokens;
    private Integer totalTokens;
    private Integer latencyMs;
    private BigDecimal cost;
    private String status;
    private String errorMessage;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    public static AiCallLog success(Long userId, String provider, String model, 
                                    int inputTokens, int outputTokens, long latencyMs) {
        AiCallLog log = new AiCallLog();
        log.setUserId(userId);
        log.setProvider(provider);
        log.setModel(model);
        log.setInputTokens(inputTokens);
        log.setOutputTokens(outputTokens);
        log.setTotalTokens(inputTokens + outputTokens);
        log.setLatencyMs((int) latencyMs);
        log.setStatus("success");
        log.setCreatedAt(LocalDateTime.now());
        return log;
    }

    public static AiCallLog failed(Long userId, String provider, String model, 
                                   String errorMessage) {
        AiCallLog log = new AiCallLog();
        log.setUserId(userId);
        log.setProvider(provider);
        log.setModel(model);
        log.setStatus("failed");
        log.setErrorMessage(errorMessage);
        log.setCreatedAt(LocalDateTime.now());
        return log;
    }
}
