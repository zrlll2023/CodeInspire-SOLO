package com.codeinspire.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {
    @NotBlank(message = "消息内容不能为空")
    private String message;
    
    private String sessionId;
    private String type = "general"; // consultation/interview/planning/general
    private String provider; // mimo/deepseek/zhipu/qwen (optional, auto-select if null)
}
