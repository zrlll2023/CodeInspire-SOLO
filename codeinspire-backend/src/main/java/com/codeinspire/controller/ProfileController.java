package com.codeinspire.controller;

import com.codeinspire.common.Result;
import com.codeinspire.dto.UserProfileDTO;
import com.codeinspire.entity.UserProfile;
import com.codeinspire.security.JwtTokenProvider;
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

    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
