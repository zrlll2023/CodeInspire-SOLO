package com.codeinspire.controller;

import com.codeinspire.common.Result;
import com.codeinspire.dto.PersonalizedAdviceResponse;
import com.codeinspire.dto.UserProfileDTO;
import com.codeinspire.entity.UserProfile;
import com.codeinspire.security.JwtTokenProvider;
import com.codeinspire.service.AdviceService;
import com.codeinspire.service.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userProfileService;
    private final AdviceService adviceService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public Result<UserProfile> getProfile(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        UserProfile profile = userProfileService.getUserProfile(userId);
        return Result.success(profile);
    }

    @PutMapping
    public Result<UserProfile> updateProfile(@Valid @RequestBody UserProfileDTO dto,
                                              HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        UserProfile profile = userProfileService.createOrUpdateProfile(userId, dto);
        return Result.success(profile);
    }

    @PostMapping("/init")
    public Result<UserProfile> initProfile(@Valid @RequestBody UserProfileDTO dto,
                                            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        UserProfile profile = userProfileService.createOrUpdateProfile(userId, dto);
        return Result.success(profile);
    }

    @DeleteMapping
    public Result<Void> deleteProfile(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        userProfileService.deleteProfile(userId);
        return Result.success();
    }

    @GetMapping("/suggestions")
    public Result<PersonalizedAdviceResponse> getSuggestions(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        UserProfile profile = userProfileService.getUserProfile(userId);
        
        if (profile == null) {
            return Result.error(400, "请先完善用户画像");
        }
        
        PersonalizedAdviceResponse advice = adviceService.generatePersonalizedAdvice(profile);
        return Result.success(advice);
    }

    @GetMapping("/career-advice")
    public Result<PersonalizedAdviceResponse> getCareerAdvice(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        UserProfile profile = userProfileService.getUserProfile(userId);
        
        if (profile == null) {
            return Result.error(400, "请先完善用户画像");
        }
        
        PersonalizedAdviceResponse advice = adviceService.generateCareerAdvice(profile);
        return Result.success(advice);
    }

    @GetMapping("/learning-path")
    public Result<PersonalizedAdviceResponse> getLearningPath(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        UserProfile profile = userProfileService.getUserProfile(userId);
        
        if (profile == null) {
            return Result.error(400, "请先完善用户画像");
        }
        
        PersonalizedAdviceResponse advice = adviceService.generateLearningPathAdvice(profile);
        return Result.success(advice);
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        String token = extractToken(request);
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
