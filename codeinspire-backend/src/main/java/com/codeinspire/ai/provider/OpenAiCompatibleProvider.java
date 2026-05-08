package com.codeinspire.ai.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class OpenAiCompatibleProvider implements AiProvider {

    private final String providerName;
    private final String apiKey;
    private final String baseUrl;
    private final String model;
    private final RestTemplate restTemplate;

    public OpenAiCompatibleProvider(String providerName, String apiKey, String baseUrl, String model) {
        this.providerName = providerName;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String getProviderName() {
        return providerName;
    }

    @Override
    public String chat(String systemPrompt, String userMessage) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = createChatBody(systemPrompt, userMessage, false);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/chat/completions",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            
            throw new RuntimeException("Invalid response from AI provider: " + providerName);
            
        } catch (HttpClientErrorException e) {
            log.error("AI API error from {}: {}", providerName, e.getResponseBodyAsString());
            throw new RuntimeException("AI API error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to call AI provider {}: {}", providerName, e.getMessage(), e);
            throw new RuntimeException("Failed to call AI provider: " + e.getMessage());
        }
    }

    @Override
    public Flux<String> chatStream(String systemPrompt, String userMessage) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = createChatBody(systemPrompt, userMessage, true);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<Flux<String>> response = restTemplate.exchange(
                    baseUrl + "/chat/completions",
                    HttpMethod.POST,
                    entity,
                    Flux.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return parseStreamResponse(response.getBody());
            }
            
            return Flux.error(new RuntimeException("Invalid streaming response"));
            
        } catch (Exception e) {
            log.error("Failed to call AI stream provider {}: {}", providerName, e.getMessage(), e);
            return Flux.error(e);
        }
    }

    private Map<String, Object> createChatBody(String systemPrompt, String userMessage, boolean stream) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("stream", stream);
        
        List<Map<String, String>> messages = new ArrayList<>();
        
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);
        }
        
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
        
        body.put("messages", messages);
        return body;
    }

    private Flux<String> parseStreamResponse(Flux<String> streamResponse) {
        return streamResponse
                .filter(line -> line.startsWith("data: ") && !line.equals("[DONE]"))
                .map(line -> line.substring(6))
                .map(data -> {
                    // Parse SSE data and extract content
                    // Simplified parsing - in production would use proper JSON parser
                    if (data.contains("\"content\"")) {
                        int start = data.indexOf("\"content\":\"") + 10;
                        int end = data.indexOf("\"", start);
                        if (start > 9 && end > start) {
                            return data.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"");
                        }
                    }
                    return "";
                })
                .filter(content -> !content.isEmpty());
    }
}
