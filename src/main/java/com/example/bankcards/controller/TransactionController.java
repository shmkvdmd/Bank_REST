package com.example.bankcards.controller;

import com.example.bankcards.dto.request.TransactionRequestDto;
import com.example.bankcards.dto.response.TransactionResponseDto;
import com.example.bankcards.service.transaction.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Transactions", description = "Transfers and transaction queries")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @Operation(summary = "Create transfer between own cards",
            description = "Creates a transfer from one own card to another own card",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = TransactionRequestDto.class))
            ),
            responses = {@ApiResponse(responseCode = "200", description = "Transfer completed"),
                    @ApiResponse(responseCode = "400", description = "Validation error")})
    @PostMapping
    public TransactionResponseDto createTransaction(@Valid @RequestBody TransactionRequestDto requestDto) {
        return transactionService.createTransaction(requestDto);
    }

    @Operation(summary = "Admin: list received transactions for user",
            description = "Admin-only: list transactions received by specified user's cards",
            responses = {@ApiResponse(responseCode = "200", description = "Paged list")})
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/received/{userId}")
    public Page<TransactionResponseDto> getReceivedTransactions(Pageable pageable,
                                                                @PathVariable(name = "userId") Long userId) {
        return transactionService.getReceivedTransactions(pageable, userId);
    }

    @Operation(summary = "Admin: list sent transactions for user",
            description = "Admin-only: list transactions sent by specified user's cards",
            responses = {@ApiResponse(responseCode = "200", description = "Paged list")})
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/sent/{userId}")
    public Page<TransactionResponseDto> getSentTransactions(Pageable pageable,
                                                            @PathVariable(name = "userId") Long userId) {
        return transactionService.getSentTransactions(pageable, userId);
    }

    @Operation(summary = "Admin: get all transactions",
            description = "Admin-only: list all transactions",
            responses = {@ApiResponse(responseCode = "200", description = "Paged list")})
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public Page<TransactionResponseDto> getAllTransactions(Pageable pageable) {
        return transactionService.getAllTransactions(pageable);
    }

    @Operation(summary = "Get own transactions",
            description = "List transactions associated with authenticated user",
            responses = {@ApiResponse(responseCode = "200", description = "Paged list")})
    @GetMapping("/own")
    public Page<TransactionResponseDto> getOwnTransactions(Pageable pageable) {
        return transactionService.getOwnTransactions(pageable);
    }
}
