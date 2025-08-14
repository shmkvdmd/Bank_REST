package com.example.bankcards.controller;

import com.example.bankcards.dto.request.AuthRequestDto;
import com.example.bankcards.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setUsername("testuser");
        requestDto.setPassword("password");

        when(authService.login(requestDto)).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer jwt-token"))
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }

    @Test
    void shouldRegisterSuccessfully() throws Exception {
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setUsername("newuser");
        requestDto.setPassword("password");

        when(authService.register(requestDto)).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newuser\",\"password\":\"password\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Authorization", "Bearer jwt-token"))
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }
}