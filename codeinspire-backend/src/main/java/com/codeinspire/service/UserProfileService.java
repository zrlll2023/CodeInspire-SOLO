package com.codeinspire.service;

import com.codeinspire.dto.UserProfileDTO;
import com.codeinspire.entity.UserProfile;

public interface UserProfileService {
    UserProfile getUserProfile(Long userId);
    UserProfile createOrUpdateProfile(Long userId, UserProfileDTO dto);
    void deleteProfile(Long userId);
}
