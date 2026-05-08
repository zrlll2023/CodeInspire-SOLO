package com.codeinspire.ai.template;

import java.util.Map;

public interface PromptTemplateService {
    String renderTemplate(String templateContent, Map<String, Object> variables);
    Prompt getPromptByScene(String scene);
    void incrementUsage(Long promptId);
}
