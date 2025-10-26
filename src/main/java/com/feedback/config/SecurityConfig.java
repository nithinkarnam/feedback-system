package com.feedback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthFilter jwtFilter = new JwtAuthFilter(jwtUtil);

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow unauthenticated GET requests to root and common static assets
                        // (read-only).
                        .requestMatchers(HttpMethod.GET, "/", "/index.html", "/favicon.ico", "/static/**", "/public/**",
                                "/webjars/**")
                        .permitAll()
                        // Auth endpoints and H2 console remain publicly accessible
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        // Admin registration requires SUPER_ADMIN authority
                        .requestMatchers("/api/admin/register").hasAuthority("SUPER_ADMIN")
                        // Other admin endpoints require ADMIN or SUPER_ADMIN authority
                        .requestMatchers("/api/admin/**").hasAnyAuthority("ADMIN", "SUPER_ADMIN")
                        // Feedback endpoints require USER role
                        .requestMatchers("/api/feedback/**").hasAuthority("USER")
                        // All other requests require authentication
                        .anyRequest().authenticated())
                // h2 console frames
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
