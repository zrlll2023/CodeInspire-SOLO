package com.codeinspire.controller;

import com.codeinspire.common.Result;
import com.codeinspire.dto.*;
import com.codeinspire.security.JwtTokenProvider;
import com.codeinspire.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/send")
    public Result<ChatResponse> sendMessage(@Valid @RequestBody ChatRequest request,
                                             HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        ChatResponse response = chatService.sendMessage(userId, request);
        return Result.success(response);
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sendMessageStream(@Valid @RequestBody ChatRequest request,
                                           HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        return chatService.sendMessageStream(userId, request);
    }

    @GetMapping("/history")
    public Result<ChatHistoryResponse> getChatHistory(@RequestParam String sessionId,
                                                      HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        ChatHistoryResponse history = chatService.getChatHistory(userId, sessionId);
        return Result.success(history);
    }

    @GetMapping("/export")
    public Result<String> exportConversation(@RequestParam Long conversationId,
                                              HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        String markdown = chatService.exportConversation(userId, conversationId);
        return Result.success(markdown);
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
