package com.example.bankcards.controller;

import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void shouldGetUserInfoSuccessfully() throws Exception {
        UserResponseDto responseDto = new UserResponseDto();

        when(userService.getInfo()).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/info"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetUserByIdSuccessfully() throws Exception {
        UserResponseDto responseDto = new UserResponseDto();

        when(userService.getUserById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}