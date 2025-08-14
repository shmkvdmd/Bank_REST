package com.example.bankcards.service.transaction;

import com.example.bankcards.dto.request.TransactionRequestDto;
import com.example.bankcards.dto.response.TransactionResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.ValidationException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.mapper.TransactionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void shouldCreateTransactionSuccessfully() {
        TransactionRequestDto requestDto = new TransactionRequestDto();
        requestDto.setSenderCardId(1L);
        requestDto.setReceiverCardId(2L);
        requestDto.setAmount(BigDecimal.valueOf(100));

        User user = new User();
        user.setId(1L);

        Card sender = new Card();
        sender.setId(1L);
        sender.setUser(user);
        sender.setBalance(BigDecimal.valueOf(200));
        sender.setStatus(CardStatus.ACTIVE);

        Card receiver = new Card();
        receiver.setId(2L);
        receiver.setUser(user);
        receiver.setBalance(BigDecimal.valueOf(50));
        receiver.setStatus(CardStatus.ACTIVE);

        Transaction transaction = new Transaction();
        transaction.setId(1L);

        TransactionResponseDto responseDto = new TransactionResponseDto();

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(cardRepository.save(sender)).thenReturn(sender);
        when(cardRepository.save(receiver)).thenReturn(receiver);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toDto(transaction)).thenReturn(responseDto);

        TransactionResponseDto result = transactionService.createTransaction(requestDto);

        assertEquals(responseDto, result);
        assertEquals(BigDecimal.valueOf(100), sender.getBalance());
        assertEquals(BigDecimal.valueOf(150), receiver.getBalance());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void shouldThrowValidationExceptionWhenCardsNotOwnedByUser() {
        TransactionRequestDto requestDto = new TransactionRequestDto();
        requestDto.setSenderCardId(1L);
        requestDto.setReceiverCardId(2L);
        requestDto.setAmount(BigDecimal.valueOf(100));

        User currentUser = new User();
        currentUser.setId(1L);

        User otherUser = new User();
        otherUser.setId(2L);

        Card sender = new Card();
        sender.setId(1L);
        sender.setUser(otherUser);

        Card receiver = new Card();
        receiver.setId(2L);
        receiver.setUser(currentUser);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(receiver));

        assertThrows(ValidationException.class, () -> transactionService.createTransaction(requestDto));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenReceiverCardNotActive() {
        TransactionRequestDto requestDto = new TransactionRequestDto();
        requestDto.setSenderCardId(1L);
        requestDto.setReceiverCardId(2L);
        requestDto.setAmount(BigDecimal.valueOf(100));

        User user = new User();
        user.setId(1L);

        Card sender = new Card();
        sender.setId(1L);
        sender.setUser(user);
        sender.setBalance(BigDecimal.valueOf(200));
        sender.setStatus(CardStatus.ACTIVE);

        Card receiver = new Card();
        receiver.setId(2L);
        receiver.setUser(user);
        receiver.setBalance(BigDecimal.valueOf(50));
        receiver.setStatus(CardStatus.BLOCKED);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(receiver));

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(requestDto));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSenderCardNotActive() {
        TransactionRequestDto requestDto = new TransactionRequestDto();
        requestDto.setSenderCardId(1L);
        requestDto.setReceiverCardId(2L);
        requestDto.setAmount(BigDecimal.valueOf(100));

        User user = new User();
        user.setId(1L);

        Card sender = new Card();
        sender.setId(1L);
        sender.setUser(user);
        sender.setBalance(BigDecimal.valueOf(200));
        sender.setStatus(CardStatus.BLOCKED);

        Card receiver = new Card();
        receiver.setId(2L);
        receiver.setUser(user);
        receiver.setBalance(BigDecimal.valueOf(50));
        receiver.setStatus(CardStatus.ACTIVE);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(receiver));

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(requestDto));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenInsufficientBalance() {
        TransactionRequestDto requestDto = new TransactionRequestDto();
        requestDto.setSenderCardId(1L);
        requestDto.setReceiverCardId(2L);
        requestDto.setAmount(BigDecimal.valueOf(300));

        User user = new User();
        user.setId(1L);

        Card sender = new Card();
        sender.setId(1L);
        sender.setUser(user);
        sender.setBalance(BigDecimal.valueOf(200));
        sender.setStatus(CardStatus.ACTIVE);

        Card receiver = new Card();
        receiver.setId(2L);
        receiver.setUser(user);
        receiver.setBalance(BigDecimal.valueOf(50));
        receiver.setStatus(CardStatus.ACTIVE);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(receiver));

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(requestDto));
    }

    @Test
    void shouldGetSentTransactionsSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);
        Long userId = 1L;

        Page<Transaction> transactionsPage = new PageImpl<>(List.of(new Transaction()));

        TransactionResponseDto responseDto = new TransactionResponseDto();

        when(transactionRepository.findBySenderCardUserId(pageable, userId)).thenReturn(transactionsPage);
        when(transactionMapper.toDto(any(Transaction.class))).thenReturn(responseDto);

        Page<TransactionResponseDto> result = transactionService.getSentTransactions(pageable, userId);

        assertEquals(1, result.getContent().size());
        verify(transactionRepository).findBySenderCardUserId(pageable, userId);
    }

    @Test
    void shouldGetReceivedTransactionsSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);
        Long userId = 1L;

        Page<Transaction> transactionsPage = new PageImpl<>(List.of(new Transaction()));

        TransactionResponseDto responseDto = new TransactionResponseDto();

        when(transactionRepository.findByReceiverCardUserId(pageable, userId)).thenReturn(transactionsPage);
        when(transactionMapper.toDto(any(Transaction.class))).thenReturn(responseDto);

        Page<TransactionResponseDto> result = transactionService.getReceivedTransactions(pageable, userId);

        assertEquals(1, result.getContent().size());
        verify(transactionRepository).findByReceiverCardUserId(pageable, userId);
    }

    @Test
    void shouldGetAllTransactionsSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Transaction> transactionsPage = new PageImpl<>(List.of(new Transaction()));

        TransactionResponseDto responseDto = new TransactionResponseDto();

        when(transactionRepository.findAll(pageable)).thenReturn(transactionsPage);
        when(transactionMapper.toDto(any(Transaction.class))).thenReturn(responseDto);

        Page<TransactionResponseDto> result = transactionService.getAllTransactions(pageable);

        assertEquals(1, result.getContent().size());
        verify(transactionRepository).findAll(pageable);
    }

    @Test
    void shouldGetOwnTransactionsSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setId(1L);

        Page<Transaction> transactionsPage = new PageImpl<>(List.of(new Transaction()));

        TransactionResponseDto responseDto = new TransactionResponseDto();

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(transactionRepository.findAllByUserId(pageable, 1L)).thenReturn(transactionsPage);
        when(transactionMapper.toDto(any(Transaction.class))).thenReturn(responseDto);

        Page<TransactionResponseDto> result = transactionService.getOwnTransactions(pageable);

        assertEquals(1, result.getContent().size());
        verify(transactionRepository).findAllByUserId(pageable, 1L);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForGetOwnTransactions() {
        Pageable pageable = PageRequest.of(0, 10);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("unknownuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> transactionService.getOwnTransactions(pageable));
    }
}