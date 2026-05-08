package com.codeinspire.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class UserProfileDTO {
    // 教育背景
    private String schoolLevel;
    private String schoolType;
    private String educationLevel;
    private String major;
    
    // 时间维度
    private String grade;
    private String urgencyLevel;
    private Integer weeklyAvailableHours;
    private String courseworkPressure;
    
    // 地理位置
    private String targetCityLevel;
    private String hometownConsideration;
    private String industryPreference;
    
    // 经济约束
    private String paymentWillingness;
    private String computerConfig;
    private String selfLearningAbility;
    private String economicPressure;
    
    // 求职目标
    private String currentStatus;
    private String majorDirection;
    private String targetPosition;
    private String targetCompany;
    private String expectedSalary;
    
    // 技术背景
    private List<Map<String, Object>> skills;
    private List<Map<String, Object>> projects;
    private List<Map<String, Object>> certifications;
}
