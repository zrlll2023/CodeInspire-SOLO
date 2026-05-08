package com.codeinspire.ai.template;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codeinspire.entity.Prompt;
import com.codeinspire.repository.PromptMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromptTemplateServiceImpl implements PromptTemplateService {

    private final PromptMapper promptMapper;

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)}}");

    @Override
    public String renderTemplate(String templateContent, Map<String, Object> variables) {
        if (templateContent == null || templateContent.isEmpty()) {
            return templateContent;
        }

        if (variables == null || variables.isEmpty()) {
            log.warn("No variables provided for template rendering");
            return templateContent;
        }

        Matcher matcher = VARIABLE_PATTERN.matcher(templateContent);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = variables.get(variableName);
            
            if (value != null) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(value.toString()));
            } else {
                log.warn("Variable '{}' not found in provided variables", variableName);
                matcher.appendReplacement(sb, Matcher.quoteReplacement("{{" + variableName + "}}"));
            }
        }
        matcher.appendTail(sb);

        String renderedTemplate = sb.toString();
        log.debug("Template rendered successfully. Variables replaced: {}", variables.size());
        
        return renderedTemplate;
    }

    @Override
    public Prompt getPromptByScene(String scene) {
        return promptMapper.selectOne(
                new LambdaQueryWrapper<Prompt>()
                        .eq(Prompt::getScene, scene)
                        .eq(Prompt::getStatus, "active")
                        .orderByDesc(Prompt::getVersion)
                        .last("LIMIT 1")
        );
    }

    @Override
    public void incrementUsage(Long promptId) {
        Prompt prompt = promptMapper.selectById(promptId);
        if (prompt != null) {
            prompt.setUsageCount(prompt.getUsageCount() + 1);
            promptMapper.updateById(prompt);
        }
    }
}
