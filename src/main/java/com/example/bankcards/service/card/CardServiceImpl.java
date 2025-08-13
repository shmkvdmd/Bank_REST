package com.example.bankcards.service.card;

import com.example.bankcards.constants.ExceptionConstants;
import com.example.bankcards.constants.LogConstants;
import com.example.bankcards.dto.request.CardCreateRequestDto;
import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.UnauthorizedOperationException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardEncryptor;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.mapper.CardMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardEncryptor cardEncryptor;
    private final CardMapper cardMapper;

    @Override
    public CardResponseDto createCard(CardCreateRequestDto request) throws Exception {
        log.info(LogConstants.CARD_CREATE_START, request.getUserId(), request.getInitialBalance(),
                request.getExpirationDate());
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> {
            log.warn(LogConstants.USER_GET_ERROR, request.getUserId());
            return new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_ID, request.getUserId()));
        });

        String cardNumber;
        String encryptedNumber;
        do {
            cardNumber = CardNumberGenerator.generateCardNumber();
            encryptedNumber = cardEncryptor.encrypt(cardNumber);
        } while (cardRepository.existsByNumberEncrypted(encryptedNumber));

        Card card = Card.builder()
                .numberEncrypted(encryptedNumber)
                .numberLast(cardNumber.substring(cardNumber.length() - 4))
                .user(user)
                .balance(BigDecimal.ZERO)
                .expiration(request.getExpirationDate() != null ? request.getExpirationDate() :
                        LocalDate.now().plusYears(4))
                .status(CardStatus.ACTIVE)
                .build();
        Card createdCard = cardRepository.save(card);
        log.info(LogConstants.CARD_CREATE_SUCCESS, createdCard.getId(), user.getUsername(), createdCard.getNumberLast());
        return cardMapper.toDtoMasked(createdCard);
    }

    @Override
    public CardResponseDto getCardById(Long cardId) {
        log.info(LogConstants.CARD_GET_START, cardId);
        Card card = cardRepository.findById(cardId).orElseThrow(() -> {
            log.warn(LogConstants.CARD_GET_ERROR, cardId);
            return new NotFoundException(String.format(ExceptionConstants.CARD_NOT_FOUND_BY_ID, cardId));
        });
        checkAccessToCard(card);
        log.info(LogConstants.CARD_GET_SUCCESS, card.getId(), card.getUser().getUsername(), card.getNumberLast());
        return cardMapper.toDto(card);
    }

    @Override
    public CardResponseDto blockCard(Long cardId) {
        log.info(LogConstants.CARD_BLOCK_START, cardId, SecurityContextHolder.getContext().getAuthentication().getName());
        Card card = cardRepository.findById(cardId).orElseThrow(() -> {
            log.warn(LogConstants.CARD_BLOCK_ERROR, cardId);
            return new NotFoundException(String.format(ExceptionConstants.CARD_NOT_FOUND_BY_ID, cardId));
        });
        checkAccessToCard(card);
        card.setStatus(CardStatus.BLOCKED);
        Card saved = cardRepository.save(card);
        log.info(LogConstants.CARD_BLOCK_SUCCESS, saved.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName());
        return cardMapper.toDto(saved);
    }

    @Override
    public CardResponseDto activateCard(Long cardId) {
        log.info(LogConstants.CARD_ACTIVATE_START, cardId,
                SecurityContextHolder.getContext().getAuthentication().getName());
        Card card = cardRepository.findById(cardId).orElseThrow(() -> {
            log.warn(LogConstants.CARD_ACTIVATE_ERROR, cardId);
            return new NotFoundException(String.format(ExceptionConstants.CARD_NOT_FOUND_BY_ID, cardId));
        });
        card.setStatus(CardStatus.ACTIVE);
        Card saved = cardRepository.save(card);
        log.info(LogConstants.CARD_ACTIVATE_SUCCESS, saved.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName());
        return cardMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        log.info(LogConstants.CARD_DELETE_START, cardId, SecurityContextHolder.getContext().getAuthentication().getName());
        Card card = cardRepository.findById(cardId).orElseThrow(() -> {
            log.warn(LogConstants.CARD_DELETE_ERROR, cardId);
            return new NotFoundException(String.format(ExceptionConstants.CARD_NOT_FOUND_BY_ID, cardId));
        });
        cardRepository.deleteById(cardId);
        log.info(LogConstants.CARD_DELETE_SUCCESS, cardId);
    }

    @Override
    public Page<CardResponseDto> getAllCards(Pageable pageable, String username) {
        User user = null;
        if (username != null) {
            user = userRepository.findByUsername(username).orElseThrow(() -> {
                log.warn(LogConstants.USER_GET_ERROR, username);
                return new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_USERNAME, username));
            });
        }
        Page<Card> cards;
        if (user != null) {
            cards = cardRepository.findAllByUserId(pageable, user.getId());
        } else {
            cards = cardRepository.findAll(pageable);
        }
        return cards.map(cardMapper::toDto);
    }

    @Override
    public Page<CardResponseDto> getOwnCards(Pageable pageable, String cardStatus) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUsername).orElseThrow(() -> {
            log.warn(LogConstants.USER_GET_ERROR, currentUsername);
            return new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_USERNAME, currentUsername));
        });
        Page<Card> cards;
        if (cardStatus != null) {
            try {
                cards = cardRepository.findAllByUserIdAndStatus(pageable, user.getId(), CardStatus.valueOf(cardStatus));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(String.format(ExceptionConstants.INVALID_STATE, cardStatus));
            }
        } else {
            cards = cardRepository.findAllByUserId(pageable, user.getId());
        }
        return cards.map(cardMapper::toDto);
    }

    public void checkAccessToCard(Card card) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.warn(LogConstants.USER_GET_ERROR, username);
            return new NotFoundException(String.format(ExceptionConstants.USER_NOT_FOUND_BY_USERNAME, username));
        });

        if (!user.getRole().equals(Role.ADMIN) && !username.equals(card.getUser().getUsername())) {
            log.warn(LogConstants.ACCESS_DENIED, username);
            throw new UnauthorizedOperationException(ExceptionConstants.UNAUTHORIZED_OPERATION);
        }
    }
}
