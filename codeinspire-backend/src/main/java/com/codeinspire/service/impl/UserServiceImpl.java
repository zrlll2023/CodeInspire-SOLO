package com.codeinspire.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codeinspire.dto.AuthResponse;
import com.codeinspire.dto.LoginRequest;
import com.codeinspire.dto.RegisterRequest;
import com.codeinspire.entity.User;
import com.codeinspire.exception.BusinessException;
import com.codeinspire.repository.UserMapper;
import com.codeinspire.security.JwtTokenProvider;
import com.codeinspire.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (existsByUsername(request.getUsername())) {
            throw new BusinessException(400, "用户名已存在");
        }

        if (existsByEmail(request.getEmail())) {
            throw new BusinessException(400, "邮箱已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        userMapper.insert(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = findUserByUsernameOrEmail(request.getUsername());

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .expiresIn(jwtTokenProvider.getExpirationTime())
                .build();
    }

    private boolean existsByUsername(String username) {
        return userMapper.exists(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
    }

    private boolean existsByEmail(String email) {
        return userMapper.exists(
                new LambdaQueryWrapper<User>().eq(User::getEmail, email)
        );
    }

    private User findUserByUsernameOrEmail(String usernameOrEmail) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, usernameOrEmail)
                        .or()
                        .eq(User::getEmail, usernameOrEmail)
                        .last("LIMIT 1")
        );
    }
}
