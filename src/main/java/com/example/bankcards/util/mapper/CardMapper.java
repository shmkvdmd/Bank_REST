package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardMapper {
    Card toEntity(CardResponseDto cardResponseDto);

    CardResponseDto toDto(Card card);

    default String mask(String last4) {
        return last4 == null ? "**** **** **** ****" : "**** **** **** " + last4;
    }

    default CardResponseDto toDtoMasked(Card card) {
        CardResponseDto dto = toDto(card);
        return new CardResponseDto(
                dto.getId(),
                mask(card.getNumberLast()),
                card.getUser() != null ? card.getUser().getUsername() : null,
                dto.getBalance(),
                dto.getExpirationDate(),
                dto.getStatus()
        );
    }
}