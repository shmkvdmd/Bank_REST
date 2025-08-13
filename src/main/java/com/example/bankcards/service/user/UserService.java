package com.example.bankcards.service.user;

import com.example.bankcards.dto.request.UserUpdateRequestDto;
import com.example.bankcards.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto getInfo();
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    UserResponseDto getUserById(Long id);
    UserResponseDto updateUser(UserUpdateRequestDto requestDto, Long id);
    void deleteUser(Long id);
}
