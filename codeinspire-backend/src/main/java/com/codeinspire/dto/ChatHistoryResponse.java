package com.codeinspire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageVO {
    private Long id;
    private String role; // user/assistant/system
    private String content;
    private LocalDateTime createdAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryResponse {
    private Long conversationId;
    private String sessionId;
    private String type;
    private List<ChatMessageVO> messages;
    private LocalDateTime createdAt;
}
