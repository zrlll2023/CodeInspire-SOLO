package com.codeinspire.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codeinspire.dto.UserProfileDTO;
import com.codeinspire.entity.UserProfile;
import com.codeinspire.exception.BusinessException;
import com.codeinspire.repository.UserProfileMapper;
import com.codeinspire.service.UserProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileMapper userProfileMapper;
    private final ObjectMapper objectMapper;

    @Override
    public UserProfile getUserProfile(Long userId) {
        return userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId)
        );
    }

    @Override
    public UserProfile createOrUpdateProfile(Long userId, UserProfileDTO dto) {
        UserProfile existingProfile = getUserProfile(userId);
        
        if (existingProfile != null) {
            updateProfile(existingProfile, dto);
            userProfileMapper.updateById(existingProfile);
            return existingProfile;
        } else {
            UserProfile newProfile = createNewProfile(userId, dto);
            userProfileMapper.insert(newProfile);
            return newProfile;
        }
    }

    @Override
    public void deleteProfile(Long userId) {
        userProfileMapper.delete(
                new LambdaQueryWrapper<UserProfile>().eq(UserProfile::getUserId, userId)
        );
    }

    private void updateProfile(UserProfile profile, UserProfileDTO dto) {
        profile.setSchoolLevel(dto.getSchoolLevel());
        profile.setSchoolType(dto.getSchoolType());
        profile.setEducationLevel(dto.getEducationLevel());
        profile.setMajor(dto.getMajor());
        profile.setGrade(dto.getGrade());
        profile.setUrgencyLevel(dto.getUrgencyLevel());
        profile.setWeeklyAvailableHours(dto.getWeeklyAvailableHours());
        profile.setCourseworkPressure(dto.getCourseworkPressure());
        profile.setTargetCityLevel(dto.getTargetCityLevel());
        profile.setHometownConsideration(dto.getHometownConsideration());
        profile.setIndustryPreference(dto.getIndustryPreference());
        profile.setPaymentWillingness(dto.getPaymentWillingness());
        profile.setComputerConfig(dto.getComputerConfig());
        profile.setSelfLearningAbility(dto.getSelfLearningAbility());
        profile.setEconomicPressure(dto.getEconomicPressure());
        profile.setCurrentStatus(dto.getCurrentStatus());
        profile.setMajorDirection(dto.getMajorDirection());
        profile.setTargetPosition(dto.getTargetPosition());
        profile.setTargetCompany(dto.getTargetCompany());
        profile.setExpectedSalary(dto.getExpectedSalary());
        
        try {
            if (dto.getSkills() != null) {
                profile.setSkills(objectMapper.writeValueAsString(dto.getSkills()));
            }
            if (dto.getProjects() != null) {
                profile.setProjects(objectMapper.writeValueAsString(dto.getProjects()));
            }
            if (dto.getCertifications() != null) {
                profile.setCertifications(objectMapper.writeValueAsString(dto.getCertifications()));
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize JSON data", e);
            throw new BusinessException(500, "数据序列化失败");
        }
    }

    private UserProfile createNewProfile(Long userId, UserProfileDTO dto) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        updateProfile(profile, dto);
        return profile;
    }
}
