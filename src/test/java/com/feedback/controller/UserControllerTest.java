package com.feedback.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedback.dto.AuthResponse;
import com.feedback.dto.RegisterRequest;
import com.feedback.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUserRegister() throws Exception {

        // 1. Input request
        RegisterRequest req = new RegisterRequest();
        req.setEmail("john@gmail.com");
        req.setPassword("pass123");
        req.setName("John");

        // 2. Mock response (THIS WAS MISSING)
        AuthResponse mockResp =
                new AuthResponse("token-123", "USER", "john@gmail.com");

        // 3. Mock behaviour
        Mockito.when(userService.registerUser(any(RegisterRequest.class)))
                .thenReturn(mockResp);

        // 4. Perform request + 5. Expect response
        mockMvc.perform(post("/api/auth/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-123"))
                .andExpect(jsonPath("$.email").value("john@gmail.com"));
    }
}
