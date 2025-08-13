package com.example.bankcards.controller;

import com.example.bankcards.dto.request.UserUpdateRequestDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "User management endpoints")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get own info", description = "Returns information about authenticated user",
            responses = {@ApiResponse(responseCode = "200", description = "User info")})
    @GetMapping("/info")
    public UserResponseDto getUserInfo() {
        return userService.getInfo();
    }

    @Operation(summary = "List users (admin)", description = "List all users (admin only)",
            responses = {@ApiResponse(responseCode = "200", description = "Paged list")})
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @Operation(summary = "Get user by id (admin)", description = "Get any user by id (admin only)",
            responses = {@ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "404", description = "User not found")})
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PathVariable(name = "userId") Long userId) {
        return userService.getUserById(userId);
    }

    @Operation(summary = "Update user (admin)", description = "Update user fields (admin only)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserUpdateRequestDto.class))
            ),
            responses = {@ApiResponse(responseCode = "200", description = "User updated")})
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}")
    public UserResponseDto updateUser(@RequestBody UserUpdateRequestDto request,
                                      @PathVariable(name = "userId") Long userId) {
        return userService.updateUser(request, userId);
    }

    @Operation(summary = "Delete user (admin)", description = "Delete user by id (admin only)",
            responses = {@ApiResponse(responseCode = "204", description = "Deleted")})
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(name = "userId") Long userId) {
        userService.deleteUser(userId);
    }
}
