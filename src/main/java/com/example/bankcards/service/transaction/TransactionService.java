package com.example.bankcards.service.transaction;

import com.example.bankcards.dto.request.TransactionRequestDto;
import com.example.bankcards.dto.response.TransactionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransactionService {
    TransactionResponseDto createTransaction(TransactionRequestDto req);
    Page<TransactionResponseDto> getSentTransactions(Pageable pageable, Long userId);
    Page<TransactionResponseDto> getReceivedTransactions(Pageable pageable, Long userId);
    Page<TransactionResponseDto> getAllTransactions(Pageable pageable);
    Page<TransactionResponseDto> getOwnTransactions(Pageable pageable);


}
