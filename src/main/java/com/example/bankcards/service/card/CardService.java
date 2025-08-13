package com.example.bankcards.service.card;


import com.example.bankcards.dto.request.CardCreateRequestDto;
import com.example.bankcards.dto.response.CardResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {
    CardResponseDto createCard(CardCreateRequestDto request) throws Exception;

    CardResponseDto getCardById(Long cardId);

    CardResponseDto blockCard(Long cardId);

    CardResponseDto activateCard(Long cardId);

    void deleteCard(Long cardId);

    Page<CardResponseDto> getAllCards(Pageable pageable, String username);

    Page<CardResponseDto> getOwnCards(Pageable pageable, String cardStatus);
}
