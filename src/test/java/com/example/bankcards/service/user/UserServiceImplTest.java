package com.example.bankcards.service.user;

import com.example.bankcards.dto.request.UserUpdateRequestDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldGetUserInfoSuccessfully() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserResponseDto responseDto = new UserResponseDto();

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.getInfo();

        assertEquals(responseDto, result);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForGetInfo() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("unknownuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getInfo());
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        Long id = 1L;

        User user = new User();
        user.setId(id);

        UserResponseDto responseDto = new UserResponseDto();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.getUserById(id);

        assertEquals(responseDto, result);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundById() {
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        Long id = 1L;

        UserUpdateRequestDto requestDto = new UserUpdateRequestDto();
        requestDto.setUsername("updated_username");
        requestDto.setPassword("updated_password");
        requestDto.setRole(Role.ADMIN);

        User user = new User();
        user.setId(id);
        user.setUsername("old_username");
        user.setPassword("old_password");
        user.setRole(Role.USER);

        User savedUser = new User();
        savedUser.setId(id);
        savedUser.setUsername("updated_username");
        savedUser.setPassword("hashed_updated_password");
        savedUser.setRole(Role.ADMIN);

        UserResponseDto responseDto = new UserResponseDto();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.encode("updated_password")).thenReturn("hashed_updated_password");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(responseDto);

        UserResponseDto result = userService.updateUser(requestDto, id);

        assertEquals(responseDto, result);
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForUpdate() {
        Long id = 1L;

        UserUpdateRequestDto requestDto = new UserUpdateRequestDto();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(requestDto, id));
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        Long id = 1L;

        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.deleteUser(id);

        verify(userRepository).delete(user);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForDelete() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.deleteUser(id));
    }
}