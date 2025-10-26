package com.feedback.controller;

import com.feedback.dto.AuthResponse;
import com.feedback.dto.LoginRequest;
import com.feedback.dto.RegisterRequest;
import com.feedback.entity.Admin;
import com.feedback.entity.User;
import com.feedback.repository.AdminRepository;
import com.feedback.repository.UserRepository;
import com.feedback.config.JwtUtil;
import com.feedback.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AdminService adminService;

    @PostMapping("/admin/register")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!"SUPER_ADMIN".equals(auth.getAuthorities().iterator().next().getAuthority())) {
            return ResponseEntity.status(403).body("Only SUPER_ADMIN can register new admins");
        }

        Admin admin = adminService.registerAdmin(request.getName(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new AuthResponse(
                jwtUtil.generateToken(admin.getEmail(), admin.getRole()),
                admin.getRole(),
                admin.getEmail()));
    }

    @PostMapping("/user/login")
    public ResponseEntity<AuthResponse> userLogin(@RequestBody LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), "USER");
        return ResponseEntity.ok(new AuthResponse(token, "USER", user.getEmail()));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> adminLogin(@RequestBody LoginRequest req) {
        Admin admin = adminRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!passwordEncoder.matches(req.getPassword(), admin.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(admin.getEmail(), admin.getRole());
        return ResponseEntity.ok(new AuthResponse(token, admin.getRole(), admin.getEmail()));
    }
}