package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "AuthRequest", description = "Login credentials")
public class AuthRequestDto {
    @NotNull
    @Schema(description = "Username", example = "alice")
    private String username;

    @NotNull
    @Schema(description = "Password", example = "qwerty", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;
}
