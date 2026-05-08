package com.codeinspire.ai.provider;

import reactor.core.publisher.Flux;

public interface AiProvider {
    String getProviderName();
    String chat(String systemPrompt, String userMessage);
    Flux<String> chatStream(String systemPrompt, String userMessage);
}
