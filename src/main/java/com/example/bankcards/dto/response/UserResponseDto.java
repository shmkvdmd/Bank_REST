package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "UserResponse", description = "User payload returned to client")
public class UserResponseDto {
    @Schema(description = "User id", example = "1")
    private Long id;

    @Schema(description = "Username", example = "alice")
    private String username;

    @Schema(description = "Role", example = "USER")
    private Role role;
}
