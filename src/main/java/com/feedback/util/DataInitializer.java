package com.feedback.util;

import com.feedback.entity.Admin;
import com.feedback.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (adminRepository.findByUsername("superadmin").isEmpty()) {
            Admin sa = Admin.builder()
                    .username("superadmin")
                    .email("superadmin@cfs.com")
                    .passwordHash(passwordEncoder.encode("admin@123"))
                    .role("SUPER_ADMIN")
                    .build();
            adminRepository.save(sa);
            System.out.println("Created superadmin: superadmin@cfs.com / admin@123");
        }
    }
}