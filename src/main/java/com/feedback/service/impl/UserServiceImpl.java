package com.feedback.service.impl;

import com.feedback.dto.AuthResponse;
import com.feedback.dto.RegisterRequest;
import com.feedback.entity.User;
import com.feedback.repository.UserRepository;
import com.feedback.config.JwtUtil;
import com.feedback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse registerUser(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("User already exists");
        }
        User u = User.builder()
                .email(req.getEmail())
                .name(req.getName())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .build();
        userRepository.save(u);
        String token = jwtUtil.generateToken(u.getEmail(), "USER");
        return new AuthResponse(token, "USER", u.getEmail());
    }
}
