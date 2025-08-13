package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(exclude = "user")
@ToString(exclude = "user")
@Builder
@Entity
@Table(name = "cards")
@Schema(name = "Card", description = "Bank card entity with encrypted number, owner, balance, expiration and status")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Card DB identifier", example = "1")
    private Long id;

    @Column(name = "number_encrypted", nullable = false, length = 255, unique = true)
    @NotNull
    @Schema(description = "Encrypted card number (stored encrypted)", example = "ZHVtbXlFbmNyeXB0ZWRUZXh0",
            accessMode = Schema.AccessMode.READ_ONLY)
    private String numberEncrypted;

    @Column(name = "number_last4", nullable = false, length = 4)
    @NotNull
    @Schema(description = "Last 4 digits of card number", example = "1234")
    private String numberLast;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    @Schema(description = "Owner of the card")
    private User user;

    @Column(nullable = false, precision = 15, scale = 2)
    @NotNull
    @DecimalMin(value = "0.00")
    @Schema(description = "Current balance on the card", example = "1000.50")
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    @NotNull
    @Schema(description = "Card expiration date (YYYY-MM-DD)", example = "2029-08-31")
    private LocalDate expiration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Schema(description = "Card status", example = "ACTIVE")
    private CardStatus status;
}
