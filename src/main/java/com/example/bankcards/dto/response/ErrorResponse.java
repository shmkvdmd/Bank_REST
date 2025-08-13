package com.example.bankcards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ErrorResponse", description = "Standard error response")
public class ErrorResponse {
    @Schema(description = "Error message", example = "User not found")
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Timestamp", example = "2025-08-13 15:00:00")
    private LocalDateTime timestamp;
}
