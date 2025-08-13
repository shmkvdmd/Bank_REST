package com.example.bankcards.service.transaction;

import com.example.bankcards.constants.ExceptionConstants;
import com.example.bankcards.constants.LogConstants;
import com.example.bankcards.dto.request.TransactionRequestDto;
import com.example.bankcards.dto.response.TransactionResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.TransactionStatus;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.ValidationException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.mapper.TransactionMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TransactionResponseDto createTransaction(TransactionRequestDto requestDto) {
        log.info(LogConstants.TRANSFER_START, requestDto.getSenderCardId(), requestDto.getReceiverCardId(),
                requestDto.getAmount());

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> {
            log.warn(LogConstants.USER_GET_ERROR, currentUsername);
            return new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_USERNAME, currentUsername));
        });

        Card sender = cardRepository.findById(requestDto.getSenderCardId())
                .orElseThrow(() -> {
                    log.warn(LogConstants.CARD_GET_ERROR, requestDto.getSenderCardId());
                    return new NotFoundException(String.format(ExceptionConstants.CARD_NOT_FOUND_BY_ID,
                            requestDto.getSenderCardId()));
                });
        Card receiver = cardRepository.findById(requestDto.getReceiverCardId())
                .orElseThrow(() -> {
                    log.warn(LogConstants.CARD_GET_ERROR, requestDto.getReceiverCardId());
                    return new NotFoundException(String.format(ExceptionConstants.CARD_NOT_FOUND_BY_ID,
                            requestDto.getReceiverCardId()));
                });

        if (!sender.getUser().getId().equals(currentUser.getId()) ||
                !receiver.getUser().getId().equals(currentUser.getId())) {
            log.warn(LogConstants.TRANSFER_ERROR, requestDto.getSenderCardId(), requestDto.getReceiverCardId(),
                    requestDto.getAmount());
            throw new ValidationException(ExceptionConstants.TRANSACTION_NOT_ALLOWED);
        }

        if (receiver.getStatus() != CardStatus.ACTIVE) {
            log.warn(LogConstants.TRANSFER_ERROR, sender.getId(), receiver.getId(), requestDto.getAmount(),
                    "receiver not active");
            throw new IllegalArgumentException(String.format(ExceptionConstants.CARD_NOT_ACTIVE, receiver.getId()));
        }

        if (sender.getStatus() != CardStatus.ACTIVE) {
            log.warn(LogConstants.TRANSFER_ERROR, sender.getId(), receiver.getId(), requestDto.getAmount(),
                    "sender not active");
            throw new IllegalArgumentException(String.format(ExceptionConstants.CARD_NOT_ACTIVE, sender.getId()));
        }

        if (sender.getBalance().compareTo(requestDto.getAmount()) < 0) {
            log.warn(LogConstants.AMOUNT_ERROR, sender.getId(), requestDto.getAmount(), sender.getBalance());
            throw new IllegalArgumentException(String.format(ExceptionConstants.AMOUNT_ERROR, sender.getId()));
        }

        sender.setBalance(sender.getBalance().subtract(requestDto.getAmount()));
        receiver.setBalance(receiver.getBalance().add(requestDto.getAmount()));
        cardRepository.save(sender);
        cardRepository.save(receiver);

        Transaction transaction = Transaction.builder()
                .senderCard(sender)
                .receiverCard(receiver)
                .amount(requestDto.getAmount())
                .status(TransactionStatus.COMPLETED)
                .createdAt(Instant.now())
                .build();

        transaction = transactionRepository.save(transaction);

        log.info(LogConstants.TRANSFER_SUCCESS, transaction.getId(), sender.getId(), receiver.getId(),
                transaction.getAmount());
        return transactionMapper.toDto(transaction);
    }

    @Override
    public Page<TransactionResponseDto> getSentTransactions(Pageable pageable, Long userId) {
        log.info(LogConstants.TRANSACTION_LIST_REQUEST, userId, pageable);
        return transactionRepository.findBySenderCardUserId(pageable, userId)
                .map(transactionMapper::toDto);
    }

    @Override
    public Page<TransactionResponseDto> getReceivedTransactions(Pageable pageable, Long userId) {
        log.info(LogConstants.TRANSACTION_LIST_REQUEST, userId, pageable);
        return transactionRepository.findByReceiverCardUserId(pageable, userId)
                .map(transactionMapper::toDto);
    }

    @Override
    public Page<TransactionResponseDto> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable).map(transactionMapper::toDto);
    }

    @Override
    public Page<TransactionResponseDto> getOwnTransactions(Pageable pageable) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> {
            log.warn(LogConstants.USER_GET_ERROR, currentUsername);
            return new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_USERNAME, currentUsername));
        });

        return transactionRepository.findAllByUserId(pageable, currentUser.getId())
                .map(transactionMapper::toDto);
    }
}
