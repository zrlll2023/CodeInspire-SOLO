package com.codeinspire.service;

import com.codeinspire.dto.AuthResponse;
import com.codeinspire.dto.LoginRequest;
import com.codeinspire.dto.RegisterRequest;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
