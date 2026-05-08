package com.codeinspire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalizedAdviceResponse {
    private String summary;
    private List<AdviceItem> advices;
    private RiskWarning riskWarning;
    private Map<String, Object> metadata;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdviceItem {
        private String category; // learning/career/skill/interview/project
        private String title;
        private String description;
        private int priority; // 1-5
        private String timeframe;
        private List<String> actionItems;
        private double confidence; // 0.0-1.0
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RiskWarning {
        private String level; // low/medium/high
        private String title;
        private String description;
        private List<String> recommendations;
    }
}
