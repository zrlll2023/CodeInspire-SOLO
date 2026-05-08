package com.codeinspire.controller;

import com.codeinspire.common.Result;
import com.codeinspire.dto.AuthResponse;
import com.codeinspire.dto.LoginRequest;
import com.codeinspire.dto.RegisterRequest;
import com.codeinspire.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return Result.success(response);
    }

    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return Result.success(response);
    }
}
