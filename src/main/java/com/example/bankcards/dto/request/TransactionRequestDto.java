package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "TransactionRequest", description = "Request to transfer money between two cards")
public class TransactionRequestDto {
    @NotNull
    @Schema(description = "Sender card id", example = "10")
    private Long senderCardId;

    @NotNull
    @Schema(description = "Receiver card id", example = "11")
    private Long receiverCardId;

    @NotNull
    @Positive
    @Schema(description = "Transfer amount", example = "150.00")
    private BigDecimal amount;
}
