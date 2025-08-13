package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.response.TransactionResponseDto;
import com.example.bankcards.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {
    TransactionResponseDto toDto(Transaction transaction);

    Transaction toEntity(TransactionResponseDto transactionResponseDto);
}