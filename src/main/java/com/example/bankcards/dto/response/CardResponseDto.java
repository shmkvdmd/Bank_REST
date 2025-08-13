package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Schema(name = "CardResponse", description = "Card returned to client (masked number)")
public class CardResponseDto {
    @Schema(description = "Card id", example = "123")
    private Long id;

    @Schema(description = "Masked card number", example = "**** **** **** 1234")
    private String maskedNumber;

    @Schema(description = "Owner username", example = "alice")
    private String ownerUsername;

    @Schema(description = "Current balance", example = "1000.50")
    private BigDecimal balance;

    @Schema(description = "Expiration date", example = "2029-08-31")
    private LocalDate expirationDate;

    @Schema(description = "Card status", example = "ACTIVE")
    private CardStatus status;
}
