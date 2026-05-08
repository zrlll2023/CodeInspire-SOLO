package com.codeinspire.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai.providers")
public class AiProviderProperties {

    private MimoConfig mimo = new MimoConfig();
    private DeepseekConfig deepseek = new DeepseekConfig();
    private ZhipuConfig zhipu = new ZhipuConfig();
    private QwenConfig qwen = new QwenConfig();

    @Data
    public static class MimoConfig {
        private String apiKey;
        private String baseUrl;
        private String model;
    }

    @Data
    public static class DeepseekConfig {
        private String apiKey;
        private String baseUrl;
        private String model;
    }

    @Data
    public static class ZhipuConfig {
        private String apiKey;
        private String baseUrl;
        private String model;
    }

    @Data
    public static class QwenConfig {
        private String apiKey;
        private String baseUrl;
        private String model;
    }
}
