package com.feedback.service;

import com.feedback.dto.AuthResponse;
import com.feedback.dto.RegisterRequest;

public interface UserService {
    AuthResponse registerUser(RegisterRequest req);
}

