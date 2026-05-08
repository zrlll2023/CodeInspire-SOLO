package com.codeinspire.privacy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "privacy")
public class PrivacyConfig {

    private boolean enabled = true;
    private String defaultReplacement = "[敏感信息]";
    private Map<String, String> patterns = new LinkedHashMap<>();

    public PrivacyConfig() {
        patterns.put("phone", "1[3-9]\\d{9}");
        patterns.put("email", "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        patterns.put("id_card", "\\d{17}[\\dXx]");
    }
}
