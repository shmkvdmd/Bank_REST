package com.example.bankcards.service.auth;

import com.example.bankcards.dto.request.AuthRequestDto;

public interface AuthService {
    String login(AuthRequestDto authRequest);
    String register(AuthRequestDto authRequest);
}
