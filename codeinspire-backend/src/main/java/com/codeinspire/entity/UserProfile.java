package com.codeinspire.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_profiles")
public class UserProfile {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    
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
    
    // 技术背景 (JSON)
    private String skills;
    private String projects;
    private String certifications;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
