package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.TransactionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Schema(name = "TransactionResponse", description = "Transaction details returned to client")
public class TransactionResponseDto {
    @Schema(description = "Transaction id", example = "100")
    private Long id;

    @Schema(description = "Amount transferred", example = "150.00")
    private BigDecimal amount;

    @Schema(description = "Status", example = "COMPLETED")
    private TransactionStatus status;

    @Schema(description = "Creation timestamp", example = "2025-08-13T15:00:00Z")
    private Instant createdAt;
}
