package com.codeinspire.ai.service;

import com.codeinspire.ai.provider.AiProvider;
import com.codeinspire.entity.AiCallLog;
import com.codeinspire.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final Map<String, AiProvider> aiProviders;
    private final Random random = new Random();

    public String chat(String providerName, String systemPrompt, String userMessage) {
        AiProvider provider = getProvider(providerName);
        
        long startTime = System.currentTimeMillis();
        String response;
        String status = "success";
        String errorMessage = null;
        
        try {
            response = provider.chat(systemPrompt, userMessage);
        } catch (Exception e) {
            status = "failed";
            errorMessage = e.getMessage();
            log.error("AI call failed for provider {}: {}", providerName, e.getMessage());
            throw new BusinessException(500, "AI服务调用失败: " + e.getMessage());
        } finally {
            long latency = System.currentTimeMillis() - startTime;
            logAiCall(providerName, systemPrompt, userMessage, status, latency, errorMessage);
        }
        
        return response;
    }

    public Flux<String> chatStream(String providerName, String systemPrompt, String userMessage) {
        AiProvider provider = getProvider(providerName);
        
        long startTime = System.currentTimeMillis();
        
        return provider.chatStream(systemPrompt, userMessage)
                .doOnComplete(() -> {
                    long latency = System.currentTimeMillis() - startTime;
                    logAiCall(providerName, systemPrompt, userMessage, "success", latency, null);
                })
                .onErrorResume(e -> {
                    long latency = System.currentTimeMillis() - startTime;
                    logAiCall(providerName, systemPrompt, userMessage, "failed", latency, e.getMessage());
                    return Flux.error(new BusinessException(500, "AI流式调用失败: " + e.getMessage()));
                });
    }

    public String chatWithAutoSelect(String systemPrompt, String userMessage) {
        List<String> availableProviders = aiProviders.keySet().stream().toList();
        
        if (availableProviders.isEmpty()) {
            throw new BusinessException(500, "没有可用的AI服务");
        }
        
        // Simple round-robin selection - can be enhanced with intelligent routing
        String selectedProvider = availableProviders.get(random.nextInt(availableProviders.size()));
        log.info("Auto-selected AI provider: {}", selectedProvider);
        
        return chat(selectedProvider, systemPrompt, userMessage);
    }

    private AiProvider getProvider(String providerName) {
        if (providerName == null || providerName.isEmpty()) {
            return getFirstAvailableProvider();
        }
        
        AiProvider provider = aiProviders.get(providerName.toLowerCase());
        if (provider == null) {
            throw new BusinessException(400, "不支持的AI提供商: " + providerName);
        }
        return provider;
    }

    private AiProvider getFirstAvailableProvider() {
        return aiProviders.values().stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException(500, "没有可用的AI服务"));
    }

    private void logAiCall(String provider, String systemPrompt, String userMessage,
                           String status, long latencyMs, String errorMessage) {
        // Simplified logging - in production would save to database
        log.info("AI Call Log: provider={}, status={}, latency={}ms", provider, status, latencyMs);
        
        // TODO: Save to ai_call_logs table
        /*
        AiCallLog callLog = new AiCallLog();
        callLog.setUserId(userId);
        callLog.setProvider(provider);
        callLog.setModel(provider.getModel());
        callLog.setInputTokens(calculateTokens(systemPrompt + userMessage));
        callLog.setOutputTokens(calculateTokens(response));
        callLog.setTotalTokens(callLog.getInputTokens() + callLog.getOutputTokens());
        callLog.setLatencyMs((int) latencyMs);
        callLog.setStatus(status);
        callLog.setErrorMessage(errorMessage);
        callLog.setCreatedAt(LocalDateTime.now());
        aiCallLogMapper.insert(callLog);
        */
    }
}
