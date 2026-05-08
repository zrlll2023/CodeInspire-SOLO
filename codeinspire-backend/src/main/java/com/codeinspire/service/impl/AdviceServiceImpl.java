package com.codeinspire.service.impl;

import com.codeinspire.ai.service.AiService;
import com.codeinspire.dto.PersonalizedAdviceResponse;
import com.codeinspire.entity.UserProfile;
import com.codeinspire.service.AdviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdviceServiceImpl implements AdviceService {

    private final AiService aiService;

    @Override
    public PersonalizedAdviceResponse generatePersonalizedAdvice(UserProfile userProfile) {
        String systemPrompt = buildPersonalizedSystemPrompt();
        String userMessage = buildUserProfileContext(userProfile);
        
        String aiResponse = aiService.chatWithAutoSelect(systemPrompt, userMessage);
        
        return parseAiResponse(aiResponse);
    }

    @Override
    public PersonalizedAdviceResponse generateCareerAdvice(UserProfile userProfile) {
        String systemPrompt = buildCareerSystemPrompt();
        String userMessage = buildUserProfileContext(userProfile);
        
        String aiResponse = aiService.chatWithAutoSelect(systemPrompt, userMessage);
        
        return parseAiResponse(aiResponse);
    }

    @Override
    public PersonalizedAdviceResponse generateLearningPathAdvice(UserProfile userProfile) {
        String systemPrompt = buildLearningPathSystemPrompt();
        String userMessage = buildUserProfileContext(userProfile);
        
        String aiResponse = aiService.chatWithAutoSelect(systemPrompt, userMessage);
        
        return parseAiResponse(aiResponse);
    }

    private String buildPersonalizedSystemPrompt() {
        return """
                你是CodeInspire系统的个性化建议引擎。根据用户画像信息，生成针对性的综合建议。
                
                【输出格式要求】请严格按照以下JSON格式输出：
                {
                  "summary": "整体情况概述（2-3句话）",
                  "advices": [
                    {
                      "category": "类别(learning/career/skill/interview/project)",
                      "title": "建议标题",
                      "description": "详细描述",
                      "priority": 1-5,
                      "timeframe": "时间周期",
                      "actionItems": ["具体行动1", "具体行动2"],
                      "confidence": 0.0-1.0
                    }
                  ],
                  "riskWarning": {
                    "level": "low/medium/high",
                    "title": "风险标题",
                    "description": "风险描述",
                    "recommendations": ["建议1", "建议2"]
                  }
                }
                
                【核心原则】
                1. 根据学校层次差异化建议（985/211/一本/二本/民办）
                2. 考虑年级和紧迫程度
                3. 提供可执行的具体行动项
                4. 标注置信度和优先级
                5. 识别潜在风险并给出预警
                """;
    }

    private String buildCareerSystemPrompt() {
        return """
                你是CodeInspire系统的求职就业指导专家。根据用户画像，提供精准的求职策略建议。
                
                【输出要求】
                - 针对不同背景给出差异化竞争策略
                - 分析目标岗位和企业的匹配度
                - 提供具体的简历优化、面试准备建议
                - 给出合理的薪资期望范围
                - 识别求职过程中的潜在风险
                
                请以JSON格式输出完整的职业发展建议。
                """;
    }

    private String buildLearningPathSystemPrompt() {
        return """
                你是CodeInspire系统的学习规划专家。根据用户画像，制定个性化的技术学习路线。
                
                【输出要求】
                - 根据当前技术水平设计学习路径
                - 考虑可用时间和紧迫程度
                - 分阶段设定学习目标
                - 推荐适合的学习资源
                - 设计可衡量的进度指标
                
                请以JSON格式输出完整的学习路线图。
                """;
    }

    private String buildUserProfileContext(UserProfile profile) {
        StringBuilder context = new StringBuilder();
        context.append("【用户画像数据】\n\n");
        
        if (profile.getSchoolLevel() != null) {
            context.append("学校层次: ").append(profile.getSchoolLevel()).append("\n");
        }
        if (profile.getSchoolType() != null) {
            context.append("学校类型: ").append(profile.getSchoolType()).append("\n");
        }
        if (profile.getEducationLevel() != null) {
            context.append("学历层次: ").append(profile.getEducationLevel()).append("\n");
        }
        if (profile.getMajor() != null) {
            context.append("专业: ").append(profile.getMajor()).append("\n");
        }
        if (profile.getGrade() != null) {
            context.append("年级: ").append(profile.getGrade()).append("\n");
        }
        if (profile.getUrgencyLevel() != null) {
            context.append("紧迫程度: ").append(profile.getUrgencyLevel()).append("\n");
        }
        if (profile.getWeeklyAvailableHours() != null) {
            context.append("每周可用时间: ").append(profile.getWeeklyAvailableHours()).append("小时\n");
        }
        if (profile.getTargetCityLevel() != null) {
            context.append("目标城市级别: ").append(profile.getTargetCityLevel()).append("\n");
        }
        if (profile.getTargetPosition() != null) {
            context.append("目标岗位: ").append(profile.getTargetPosition()).append("\n");
        }
        if (profile.getTargetCompany() != null) {
            context.append("目标企业: ").append(profile.getTargetCompany()).append("\n");
        }
        if (profile.getMajorDirection() != null) {
            context.append("专业方向: ").append(profile.getMajorDirection()).append("\n");
        }
        if (profile.getCurrentStatus() != null) {
            context.append("当前位置: ").append(profile.getCurrentStatus()).append("\n");
        }
        if (profile.getSkills() != null) {
            context.append("技能背景: ").append(profile.getSkills()).append("\n");
        }
        if (profile.getProjects() != null) {
            context.append("项目经验: ").append(profile.getProjects()).append("\n");
        }
        
        context.append("\n请基于以上画像数据，生成个性化建议。");
        
        return context.toString();
    }

    private PersonalizedAdviceResponse parseAiResponse(String aiResponse) {
        try {
            // 简化版解析 - 实际项目中应该使用更健壮的JSON解析器
            log.debug("Parsing AI response for personalized advice");
            
            return PersonalizedAdviceResponse.builder()
                    .summary("基于您的个人情况，AI已为您生成个性化建议")
                    .advices(new ArrayList<>())
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to parse AI response", e);
            throw new RuntimeException("解析AI响应失败: " + e.getMessage());
        }
    }
}
