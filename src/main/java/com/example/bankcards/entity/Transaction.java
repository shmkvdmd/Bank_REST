package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.TransactionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"senderCard", "receiverCard"})
@ToString(exclude = {"senderCard", "receiverCard"})
@Builder
@Entity
@Table(name = "transactions")
@Schema(name = "Transaction", description = "Money transfer between two cards")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Transaction id", example = "100")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_card_id", nullable = false)
    @NotNull
    @Schema(description = "Sender card")
    private Card senderCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_card_id", nullable = false)
    @NotNull
    @Schema(description = "Receiver card")
    private Card receiverCard;

    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull
    @Positive
    @Schema(description = "Transfer amount", example = "150.00")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Schema(description = "Transaction status", example = "COMPLETED")
    private TransactionStatus status = TransactionStatus.IN_PROCESS;

    @Column(name = "created_at", nullable = false)
    @NotNull
    @Schema(description = "Creation timestamp (ISO-8601)", example = "2025-08-13T14:30:00Z")
    private Instant createdAt;
}
