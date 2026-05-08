package com.codeinspire.ai.config;

import com.codeinspire.ai.provider.AiProvider;
import com.codeinspire.ai.provider.OpenAiCompatibleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AiProviderConfig {

    private final AiProviderProperties properties;

    @Bean(name = "mimoProvider")
    @ConditionalOnProperty(name = "ai.providers.mimo.api-key", havingValue = "not-empty")
    public AiProvider mimoProvider() {
        log.info("Initializing MiMo AI provider");
        return new OpenAiCompatibleProvider(
                "mimo",
                properties.getMimo().getApiKey(),
                properties.getMimo().getBaseUrl(),
                properties.getMimo().getModel()
        );
    }

    @Bean(name = "deepseekProvider")
    @ConditionalOnProperty(name = "ai.providers.deepseek.api-key", havingValue = "not-empty")
    public AiProvider deepseekProvider() {
        log.info("Initializing DeepSeek AI provider");
        return new OpenAiCompatibleProvider(
                "deepseek",
                properties.getDeepseek().getApiKey(),
                properties.getDeepseek().getBaseUrl(),
                properties.getDeepseek().getModel()
        );
    }

    @Bean(name = "zhipuProvider")
    @ConditionalOnProperty(name = "ai.providers.zhipu.api-key", havingValue = "not-empty")
    public AiProvider zhipuProvider() {
        log.info("Initializing Zhipu (GLM) AI provider");
        return new OpenAiCompatibleProvider(
                "zhipu",
                properties.getZhipu().getApiKey(),
                properties.getZhipu().getBaseUrl(),
                properties.getZhipu().getModel()
        );
    }

    @Bean(name = "qwenProvider")
    @ConditionalOnProperty(name = "ai.providers.qwen.api-key", havingValue = "not-empty")
    public AiProvider qwenProvider() {
        log.info("Initializing Qwen AI provider");
        return new OpenAiCompatibleProvider(
                "qwen",
                properties.getQwen().getApiKey(),
                properties.getQwen().getBaseUrl(),
                properties.getQwen().getModel()
        );
    }

    @Bean
    public Map<String, AiProvider> aiProviders(
            java.util.Optional<AiProvider> mimoProvider,
            java.util.Optional<AiProvider> deepseekProvider,
            java.util.Optional<AiProvider> zhipuProvider,
            java.util.Optional<AiProvider> qwenProvider) {

        Map<String, AiProvider> providers = new HashMap<>();
        
        mimoProvider.ifPresent(provider -> providers.put("mimo", provider));
        deepseekProvider.ifPresent(provider -> providers.put("deepseek", provider));
        zhipuProvider.ifPresent(provider -> providers.put("zhipu", provider));
        qwenProvider.ifPresent(provider -> providers.put("qwen", provider));

        log.info("Initialized {} AI provider(s)", providers.size());
        return providers;
    }
}
