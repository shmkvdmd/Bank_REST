package com.example.bankcards.service.user;

import com.example.bankcards.constants.ExceptionConstants;
import com.example.bankcards.constants.LogConstants;
import com.example.bankcards.dto.request.UserUpdateRequestDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserResponseDto getInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("getInfo for user={}", username);
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.warn(LogConstants.USER_GET_ERROR, username);
            return new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_USERNAME, username));
        });
        return userMapper.toDto(user);
    }

    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        log.info("getAllUsers pageable={}", pageable);
        List<User> users = userRepository.findAll();
        List<UserResponseDto> userList = userRepository.findAll().stream().map(userMapper::toDto).toList();
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        log.info(LogConstants.USER_GET_START, id);
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn(LogConstants.USER_GET_ERROR, id);
            return new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, id));
        });
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateUser(UserUpdateRequestDto requestDto, Long id) {
        log.info(LogConstants.USER_UPDATE_START, id);
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn(LogConstants.USER_GET_ERROR, id);
            return new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, id));
        });

        if (requestDto.getUsername() != null) {
            user.setUsername(requestDto.getUsername());
        }
        if (requestDto.getPassword() != null) {
            user.setPassword(bCryptPasswordEncoder.encode(requestDto.getPassword()));
        }
        if (requestDto.getRole() != null) {
            user.setRole(requestDto.getRole());
        }

        User saved = userRepository.save(user);
        log.info(LogConstants.USER_UPDATE_SUCCESS, saved.getId());
        return userMapper.toDto(saved);
    }

    @Override
    public void deleteUser(Long id) {
        log.info(LogConstants.USER_DELETE_START, id);
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn(LogConstants.USER_DELETE_ERROR, id);
            return new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, id));
        });
        userRepository.delete(user);
        log.info(LogConstants.USER_DELETE_SUCCESS, id);
    }
}
