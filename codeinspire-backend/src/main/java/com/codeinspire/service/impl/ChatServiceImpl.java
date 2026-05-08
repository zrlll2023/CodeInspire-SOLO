package com.codeinspire.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codeinspire.ai.service.AiService;
import com.codeinspire.dto.*;
import com.codeinspire.entity.Conversation;
import com.codeinspire.entity.Message;
import com.codeinspire.entity.UserProfile;
import com.codeinspire.privacy.PrivacyService;
import com.codeinspire.repository.ConversationMapper;
import com.codeinspire.repository.MessageMapper;
import com.codeinspire.repository.UserProfileMapper;
import com.codeinspire.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;
    private final UserProfileMapper userProfileMapper;
    private final AiService aiService;
    private final PrivacyService privacyService;

    @Override
    public ChatResponse sendMessage(Long userId, ChatRequest request) {
        String sessionId = request.getSessionId() != null ? 
                request.getSessionId() : UUID.randomUUID().toString();
        
        Conversation conversation = getOrCreateConversation(userId, sessionId, request.getType());
        
        Message userMessage = saveMessage(conversation.getId(), "user", request.getMessage());
        
        UserProfile userProfile = getUserProfile(userId);
        String systemPrompt = buildSystemPrompt(userProfile, request.getType());
        
        List<Message> recentMessages = getRecentMessages(conversation.getId(), 10);
        String contextHistory = buildContextHistory(recentMessages);
        
        PrivacyService.MaskedResult maskedResult = privacyService.mask(request.getMessage());
        
        String fullUserMessage = contextHistory + "\n\n用户: " + maskedResult.getMaskedContent();
        
        String aiResponse = aiService.chat(
                request.getProvider(),
                systemPrompt,
                fullUserMessage
        );
        
        String finalResponse = privacyService.unmask(aiResponse, maskedResult.getPiiMapping());
        
        Message assistantMessage = saveMessage(conversation.getId(), "assistant", finalResponse);
        
        return ChatResponse.builder()
                .response(finalResponse)
                .sessionId(sessionId)
                .conversationId(conversation.getId())
                .provider(request.getProvider())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @Override
    public Flux<String> sendMessageStream(Long userId, ChatRequest request) {
        String sessionId = request.getSessionId() != null ? 
                request.getSessionId() : UUID.randomUUID().toString();
        
        Conversation conversation = getOrCreateConversation(userId, sessionId, request.getType());
        
        Message userMessage = saveMessage(conversation.getId(), "user", request.getMessage());
        
        UserProfile userProfile = getUserProfile(userId);
        String systemPrompt = buildSystemPrompt(userProfile, request.getType());
        
        List<Message> recentMessages = getRecentMessages(conversation.getId(), 10);
        String contextHistory = buildContextHistory(recentMessages);
        
        PrivacyService.MaskedResult maskedResult = privacyService.mask(request.getMessage());
        
        String fullUserMessage = contextHistory + "\n\n用户: " + maskedResult.getMaskedContent();
        
        return aiService.chatStream(request.getProvider(), systemPrompt, fullUserMessage)
                .map(chunk -> privacyService.unmask(chunk, maskedResult.getPiiMapping()))
                .doOnComplete(() -> {
                    log.info("Stream completed for session: {}", sessionId);
                });
    }

    @Override
    public ChatHistoryResponse getChatHistory(Long userId, String sessionId) {
        Conversation conversation = conversationMapper.selectOne(
                new LambdaQueryWrapper<Conversation>()
                        .eq(Conversation::getUserId, userId)
                        .eq(Conversation::getSessionId, sessionId)
                        .last("LIMIT 1")
        );
        
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }
        
        List<Message> messages = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getConversationId, conversation.getId())
                        .orderByAsc(Message::getCreatedAt)
        );
        
        List<ChatMessageVO> messageVOs = messages.stream()
                .map(msg -> ChatMessageVO.builder()
                        .id(msg.getId())
                        .role(msg.getRole())
                        .content(msg.getContent())
                        .createdAt(msg.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        return ChatHistoryResponse.builder()
                .conversationId(conversation.getId())
                .sessionId(sessionId)
                .type(conversation.getType())
                .messages(messageVOs)
                .createdAt(conversation.getCreatedAt())
                .build();
    }

    @Override
    public String exportConversation(Long userId, Long conversationId) {
        Conversation conversation = conversationMapper.selectById(conversationId);
        
        if (conversation == null || !conversation.getUserId().equals(userId)) {
            throw new RuntimeException("对话不存在或无权访问");
        }
        
        List<Message> messages = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getConversationId, conversationId)
                        .orderByAsc(Message::getCreatedAt)
        );
        
        StringBuilder markdown = new StringBuilder();
        markdown.append("# 对话记录导出\n\n");
        markdown.append("**会话ID**: ").append(conversation.getSessionId()).append("\n");
        markdown.append("**类型**: ").append(conversation.getType()).append("\n");
        markdown.append("**时间**: ").append(conversation.getCreatedAt()).append("\n\n");
        markdown.append("---\n\n");
        
        for (Message msg : messages) {
            String roleName = "user".equals(msg.getRole()) ? "👤 用户" : "🤖 AI助手";
            markdown.append("## ").append(roleName).append("\n\n");
            markdown.append(msg.getContent()).append("\n\n");
        }
        
        return markdown.toString();
    }

    private Conversation getOrCreateConversation(Long userId, String sessionId, String type) {
        Conversation existing = conversationMapper.selectOne(
                new LambdaQueryWrapper<Conversation>()
                        .eq(Conversation::getUserId, userId)
                        .eq(Conversation::getSessionId, sessionId)
                        .last("LIMIT 1")
        );
        
        if (existing != null) {
            return existing;
        }
        
        Conversation newConversation = new Conversation();
        newConversation.setUserId(userId);
        newConversation.setSessionId(sessionId);
        newConversation.setType(type);
        conversationMapper.insert(newConversation);
        
        return newConversation;
    }

    private Message saveMessage(Long conversationId, String role, String content) {
        Message message = new Message();
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        messageMapper.insert(message);
        return message;
    }

    private UserProfile getUserProfile(Long userId) {
        return userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId)
        );
    }

    private List<Message> getRecentMessages(Long conversationId, int limit) {
        return messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getConversationId, conversationId)
                        .orderByDesc(Message::getCreatedAt)
                        .last("LIMIT " + limit)
        ).stream()
         .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
         .collect(Collectors.toList());
    }

    private String buildContextHistory(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return "";
        }
        
        StringBuilder history = new StringBuilder();
        history.append("[历史对话上下文]\n");
        
        for (Message msg : messages) {
            String roleLabel = "user".equals(msg.getRole()) ? "用户" : "AI助手";
            history.append(roleLabel).append(": ").append(msg.getContent()).append("\n");
        }
        
        return history.toString();
    }

    private String buildSystemPrompt(UserProfile userProfile, String type) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("你是CodeInspire - 一个专业的计算机专业学生AI个性化顾问系统。\n");
        prompt.append("你的目标是为计算机专业学生提供精准的技术路线、求职策略和成长规划建议。\n\n");
        
        prompt.append("【核心原则】\n");
        prompt.append("1. 不编造信息：不编造薪资、企业招聘、技术趋势等数据\n");
        prompt.append("2. 信息不足必须追问：缺少关键信息时主动询问\n");
        prompt.append("3. 低置信度必须提示：对不确定的信息标注置信度\n\n");
        
        if (userProfile != null) {
            prompt.append("【用户画像信息】\n");
            
            if (userProfile.getSchoolLevel() != null) {
                prompt.append("- 学校层次: ").append(userProfile.getSchoolLevel()).append("\n");
            }
            if (userProfile.getGrade() != null) {
                prompt.append("- 年级: ").append(userProfile.getGrade()).append("\n");
            }
            if (userProfile.getMajor() != null) {
                prompt.append("- 专业: ").append(userProfile.getMajor()).append("\n");
            }
            if (userProfile.getTargetPosition() != null) {
                prompt.append("- 目标岗位: ").append(userProfile.getTargetPosition()).append("\n");
            }
            if (userProfile.getTargetCityLevel() != null) {
                prompt.append("- 目标城市级别: ").append(userProfile.getTargetCityLevel()).append("\n");
            }
            if (userProfile.getUrgencyLevel() != null) {
                prompt.append("- 紧迫程度: ").append(userProfile.getUrgencyLevel()).append("\n");
            }
            
            prompt.append("\n请根据以上用户画像信息，提供个性化的建议。\n");
        }
        
        switch (type) {
            case "consultation":
                prompt.append("\n【当前场景】咨询模式 - 提供专业的技术、学习或职业咨询建议。\n");
                break;
            case "interview":
                prompt.append("\n【当前场景】面试准备模式 - 提供面试题解析和答题技巧。\n");
                break;
            case "planning":
                prompt.append("\n【当前场景】学习规划模式 - 帮助制定学习计划和成长路径。\n");
                break;
            default:
                prompt.append("\n【当前场景】通用对话模式 - 回答用户的各类问题。\n");
        }
        
        prompt.append("\n【回答结构要求】\n");
        prompt.append("1. 问题分析\n");
        prompt.append("2. 当前阶段判断\n");
        prompt.append("3. 推荐方案\n");
        prompt.append("4. 风险提醒\n");
        prompt.append("5. 下一步行动\n");
        
        return prompt.toString();
    }
}
