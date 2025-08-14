package com.example.bankcards.service.auth;

import com.example.bankcards.dto.request.AuthRequestDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setUsername("testuser");
        requestDto.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtProvider.generateToken("testuser")).thenReturn("jwt-token");

        String token = authService.login(requestDto);

        assertEquals("jwt-token", token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtProvider).generateToken("testuser");
    }

    @Test
    void shouldThrowExceptionWhenDataInvalid() {
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setUsername("testuser");
        requestDto.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        assertThrows(RuntimeException.class, () -> authService.login(requestDto));
    }

    @Test
    void shouldRegisterSuccessfullyWhenUsernameIsAvailable() {
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setUsername("newuser");
        requestDto.setPassword("password");

        when(userRepository.existsUserByUsername("newuser")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("password")).thenReturn("hashedpassword");
        User savedUser = new User();
        savedUser.setUsername("newuser");
        savedUser.setPassword("hashedpassword");
        savedUser.setRole(Role.USER);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtProvider.generateToken("newuser")).thenReturn("jwt-token");

        String token = authService.register(requestDto);

        assertEquals("jwt-token", token);
        verify(userRepository).existsUserByUsername("newuser");
        verify(bCryptPasswordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
        verify(jwtProvider).generateToken("newuser");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUsernameIsAlreadyTaken() {
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setUsername("existinguser");
        requestDto.setPassword("password");

        when(userRepository.existsUserByUsername("existinguser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(requestDto));
        verify(userRepository).existsUserByUsername("existinguser");
    }
}