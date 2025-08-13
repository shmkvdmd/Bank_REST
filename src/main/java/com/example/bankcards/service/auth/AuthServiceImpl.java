package com.example.bankcards.service.auth;

import com.example.bankcards.constants.ExceptionConstants;
import com.example.bankcards.constants.LogConstants;
import com.example.bankcards.dto.request.AuthRequestDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.security.JwtProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public String login(AuthRequestDto authRequestDto) {
        log.info(LogConstants.AUTH_LOGIN_ATTEMPT, authRequestDto.getUsername());
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        authRequestDto.getUsername(),
                        authRequestDto.getPassword()
                ));
        String username = ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        String token = jwtProvider.generateToken(username);
        log.info(LogConstants.AUTH_LOGIN_SUCCESS, username);
        return token;
    }

    @Override
    @Transactional
    public String register(AuthRequestDto authRequestDto) {
        log.info(LogConstants.AUTH_REGISTER_START, authRequestDto.getUsername());
        if (userRepository.existsUserByUsername(authRequestDto.getUsername())) {
            log.warn(LogConstants.AUTH_REGISTER_FAILURE, authRequestDto.getUsername(), "taken");
            throw new IllegalArgumentException(String.format(ExceptionConstants.USERNAME_ALREADY_TAKEN,
                    authRequestDto.getUsername()));
        } else {
            User newUser = User.builder()
                    .username(authRequestDto.getUsername())
                    .password(bCryptPasswordEncoder.encode(authRequestDto.getPassword()))
                    .role(Role.USER)
                    .build();
            userRepository.save(newUser);
            String token = jwtProvider.generateToken(newUser.getUsername());
            log.info(LogConstants.AUTH_REGISTER_SUCCESS, newUser.getUsername());
            return token;
        }
    }
}
