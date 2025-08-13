package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(name = "CardCreateRequest", description = "Request to create a new bank card")
public class CardCreateRequestDto {
    @NotNull
    @Schema(description = "User id to create the card for (ADMIN only)", example = "1")
    private Long userId;

    @NotNull
    @DecimalMin("0.0")
    @Schema(description = "Initial balance for the new card", example = "1000.00")
    private BigDecimal initialBalance;

    @NotNull
    @Schema(description = "Expiration date in ISO format (YYYY-MM-DD)", example = "2029-08-31")
    private LocalDate expirationDate;
}
