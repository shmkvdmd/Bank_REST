package com.example.bankcards.config;

import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${ADMIN_USERNAME:admin}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD:admin}")
    private String adminPassword;

    @Value("${ADMIN_ROLE:ADMIN}")
    private String adminRole;

    @Bean
    CommandLineRunner createDefaultAdmin() {
        return args -> {
            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                User admin = User.builder()
                        .username(adminUsername)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(Role.valueOf(adminRole))
                        .build();
                userRepository.save(admin);
            }
        };
    }
}
