package com.codeinspire.websocket;

import com.codeinspire.dto.ChatRequest;
import com.codeinspire.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketHandler {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/{sessionId}")
    public void handleChatMessage(@DestinationVariable String sessionId, 
                                   ChatRequest request) {
        log.info("Received WebSocket message for session: {}", sessionId);
        request.setSessionId(sessionId);
        
        Long userId = 1L; // TODO: 从SecurityContext获取实际用户ID
        
        Flux<String> responseStream = chatService.sendMessageStream(userId, request);
        
        responseStream.subscribe(
                chunk -> {
                    messagingTemplate.convertAndSend(
                            "/topic/chat/" + sessionId,
                            new WebSocketMessage("assistant", chunk)
                    );
                },
                error -> {
                    log.error("WebSocket stream error for session {}: {}", sessionId, error.getMessage());
                    messagingTemplate.convertAndSend(
                            "/topic/chat/" + sessionId,
                            new WebSocketMessage("error", "AI服务错误: " + error.getMessage())
                    );
                },
                () -> {
                    log.info("WebSocket stream completed for session: {}", sessionId);
                    messagingTemplate.convertAndSend(
                            "/topic/chat/" + sessionId,
                            new WebSocketMessage("done", null)
                    );
                }
        );
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class WebSocketMessage {
        private String type; // assistant/error/done
        private String content;
    }
}
