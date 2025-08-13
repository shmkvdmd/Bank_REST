package com.example.bankcards.controller;

import com.example.bankcards.dto.request.AuthRequestDto;
import com.example.bankcards.dto.response.AuthResponseDto;
import com.example.bankcards.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Login and registration endpoints")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "User login",
            description = "Authenticate user and return JWT token in Authorization header",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authentication successful"),
                    @ApiResponse(responseCode = "400", description = "Invalid credentials")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto authRequestDto) {
        String token = authService.login(authRequestDto);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(new AuthResponseDto(token));
    }

    @Operation(
            summary = "Register new user",
            description = "Register a new user (default role = USER) and return JWT token",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered"),
                    @ApiResponse(responseCode = "400", description = "Validation error")
            }
    )
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody AuthRequestDto authRequestDto) {
        String token = authService.register(authRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(new AuthResponseDto(token));
    }
}
