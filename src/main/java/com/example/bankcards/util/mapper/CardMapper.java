package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardMapper {

    @Mapping(target = "expirationDate", source = "expiration")
    @Mapping(target = "ownerUsername", source = "user.username")
    CardResponseDto toDto(Card card);

    default String mask(String last4) {
        return last4 == null ? "**** **** **** ****" : "**** **** **** " + last4;
    }

    default CardResponseDto toDtoMasked(Card card) {
        if (card == null) return null;
        CardResponseDto dto = new CardResponseDto();
        dto.setId(card.getId());
        dto.setMaskedNumber(mask(card.getNumberLast()));
        dto.setOwnerUsername(card.getUser() != null ? card.getUser().getUsername() : null);
        dto.setBalance(card.getBalance());
        dto.setExpirationDate(card.getExpiration());
        dto.setStatus(card.getStatus());
        return dto;
    }
}
