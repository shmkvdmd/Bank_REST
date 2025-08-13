package com.example.bankcards.dto.request;

import com.example.bankcards.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "UserUpdateRequest", description = "Request for updating user profile (admin can change role)")
public class UserUpdateRequestDto {
    @Schema(description = "New username", example = "bob")
    private String username;

    @Schema(description = "New password", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Schema(description = "New role (ADMIN or USER)", example = "USER")
    private Role role;
}
