package com.codeinspire.service;

import com.codeinspire.dto.ChatRequest;
import com.codeinspire.dto.ChatResponse;
import com.codeinspire.dto.ChatHistoryResponse;
import reactor.core.publisher.Flux;

public interface ChatService {
    ChatResponse sendMessage(Long userId, ChatRequest request);
    Flux<String> sendMessageStream(Long userId, ChatRequest request);
    ChatHistoryResponse getChatHistory(Long userId, String sessionId);
    String exportConversation(Long userId, Long conversationId);
}
