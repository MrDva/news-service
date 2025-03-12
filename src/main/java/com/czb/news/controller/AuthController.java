package com.czb.news.controller;

import com.alibaba.fastjson.JSON;
import com.czb.news.config.JwtUtil;
import com.czb.news.entity.User;
import com.czb.news.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 用户登录接口，验证用户名密码并返回 JWT
     * @param request 包含用户名和密码的请求体
     * @return JWT token
     */
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        logger.info("登录请求：{}",request.toString());
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        logger.info("验证权限：{}",auth);// 验证用户名和密码
        return jwtUtil.generateToken(auth.getName());          // 生成并返回 JWT
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 加密密码
        user.setEmail(request.getEmail());
        user.setPremium(false); // 默认非付费用户
        user.setPreferences("[]"); // 默认空偏好

        // 保存到数据库
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }
}

