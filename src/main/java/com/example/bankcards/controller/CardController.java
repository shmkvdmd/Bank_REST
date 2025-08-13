package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CardCreateRequestDto;
import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.service.card.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "Cards", description = "Card management endpoints (admin & user)")
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @Operation(summary = "Create card (admin)",
            description = "Creates a new card for given user (admin only)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CardCreateRequestDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Card created"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponseDto createCard(@RequestBody @Valid CardCreateRequestDto request) throws Exception {
        return cardService.createCard(request);
    }

    @Operation(summary = "List all cards (admin)",
            description = "List all cards, optionally filter by owner username. Admin only.",
            responses = {@ApiResponse(responseCode = "200", description = "Paged list")})
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<CardResponseDto> getAllCards(Pageable pageable,
                                             @RequestParam(name = "username", required = false) String username) {
        return cardService.getAllCards(pageable, username);
    }

    @Operation(summary = "List own cards",
            description = "List cards of the authenticated user (filter by status optional)",
            responses = {@ApiResponse(responseCode = "200", description = "Paged list")})
    @GetMapping("/own")
    public Page<CardResponseDto> getOwnCards(Pageable pageable,
                                             @RequestParam(name = "status", required = false) String cardStatus) {
        return cardService.getOwnCards(pageable, cardStatus);
    }

    @Operation(summary = "Get card by id",
            description = "Returns a single card (owner or admin)",
            responses = {@ApiResponse(responseCode = "200", description = "Card found"),
                    @ApiResponse(responseCode = "404", description = "Card not found")})
    @GetMapping("/{cardId}")
    public CardResponseDto getCard(@PathVariable(name = "cardId") Long cardId) {
        return cardService.getCardById(cardId);
    }

    @Operation(summary = "Get card balance",
            description = "Returns balance of card (owner or admin)",
            responses = {@ApiResponse(responseCode = "200", description = "Balance returned")})
    @GetMapping("/{cardId}/balance")
    public BigDecimal getCardBalance(@PathVariable(name = "cardId") Long cardId) {
        return cardService.getCardById(cardId).getBalance();
    }

    @Operation(summary = "Request block card (owner/admin)",
            description = "Owner or admin can request to block a card",
            responses = {@ApiResponse(responseCode = "200", description = "Card blocked")})
    @PatchMapping("/{cardId}/block")
    public CardResponseDto blockCard(@PathVariable(name = "cardId") Long cardId) {
        return cardService.blockCard(cardId);
    }

    @Operation(summary = "Activate card (admin)",
            description = "Activate a blocked or expired card (admin only)",
            responses = {@ApiResponse(responseCode = "200", description = "Card activated")})
    @PatchMapping("/{cardId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public CardResponseDto activateCard(@PathVariable(name = "cardId") Long cardId) {
        return cardService.activateCard(cardId);
    }

    @Operation(summary = "Delete card (admin)",
            description = "Delete a card by id (admin only)",
            responses = {@ApiResponse(responseCode = "204", description = "Card deleted")})
    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable(name = "cardId") Long cardId) {
        cardService.deleteCard(cardId);
    }
}
