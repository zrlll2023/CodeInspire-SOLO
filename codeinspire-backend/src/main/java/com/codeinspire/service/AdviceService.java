package com.codeinspire.service;

import com.codeinspire.dto.PersonalizedAdviceResponse;
import com.codeinspire.entity.UserProfile;

public interface AdviceService {
    PersonalizedAdviceResponse generatePersonalizedAdvice(UserProfile userProfile);
    PersonalizedAdviceResponse generateCareerAdvice(UserProfile userProfile);
    PersonalizedAdviceResponse generateLearningPathAdvice(UserProfile userProfile);
}
