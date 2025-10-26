package com.feedback.controller;

import com.feedback.dto.AuthResponse;
import com.feedback.dto.RegisterRequest;
import com.feedback.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/user/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {
        AuthResponse resp = userService.registerUser(req);
        return ResponseEntity.ok(resp);
    }

    // For brevity, login endpoints implemented via AuthController below if needed
}