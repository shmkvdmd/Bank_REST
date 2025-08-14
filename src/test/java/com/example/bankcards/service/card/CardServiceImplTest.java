package com.example.bankcards.service.card;

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
import com.example.bankcards.util.mapper.CardMapper;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardEncryptor cardEncryptor;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void shouldCreateCardSuccessfully() throws Exception {
        CardCreateRequestDto request = new CardCreateRequestDto();
        request.setUserId(1L);
        request.setExpirationDate(LocalDate.now().plusYears(4));

        User user = new User();
        user.setId(1L);

        Card card = new Card();
        card.setId(1L);
        card.setUser(user);
        card.setStatus(CardStatus.ACTIVE);

        CardResponseDto responseDto = new CardResponseDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardRepository.existsByNumberEncrypted(anyString())).thenReturn(false);
        when(cardEncryptor.encrypt(anyString())).thenReturn("encrypted-number");
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDtoMasked(card)).thenReturn(responseDto);

        CardResponseDto result = cardService.createCard(request);

        assertEquals(responseDto, result);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForCreateCard() {
        CardCreateRequestDto request = new CardCreateRequestDto();
        request.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.createCard(request));
    }

    @Test
    void shouldGetCardByIdSuccessfully() {
        Card card = new Card();
        card.setId(1L);
        User user = new User();
        user.setUsername("testuser");
        user.setRole(Role.USER);
        card.setUser(user);

        CardResponseDto responseDto = new CardResponseDto();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toDto(card)).thenReturn(responseDto);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        CardResponseDto result = cardService.getCardById(1L);

        assertEquals(responseDto, result);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCardNotFoundById() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.getCardById(1L));
    }

    @Test
    void shouldThrowUnauthorizedOperationExceptionWhenUnauthorizedAccessToCard() {
        Card card = new Card();
        card.setId(1L);
        User cardUser = new User();
        cardUser.setUsername("otheruser");
        cardUser.setRole(Role.USER);
        card.setUser(cardUser);

        User currentUser = new User();
        currentUser.setUsername("testuser");
        currentUser.setRole(Role.USER);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));

        assertThrows(UnauthorizedOperationException.class, () -> cardService.getCardById(1L));
    }

    @Test
    void shouldBlockCardSuccessfully() {
        Card card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.ACTIVE);
        User user = new User();
        user.setUsername("testuser");
        user.setRole(Role.USER);
        card.setUser(user);

        Card blockedCard = new Card();
        blockedCard.setId(1L);
        blockedCard.setStatus(CardStatus.BLOCKED);
        blockedCard.setUser(user);

        CardResponseDto responseDto = new CardResponseDto();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(blockedCard);
        when(cardMapper.toDto(blockedCard)).thenReturn(responseDto);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        CardResponseDto result = cardService.blockCard(1L);

        assertEquals(responseDto, result);
        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCardNotFoundForBlock() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.blockCard(1L));
    }

    @Test
    void shouldActivateCardSuccessfully() {
        Card card = new Card();
        card.setId(1L);
        card.setStatus(CardStatus.BLOCKED);

        Card activatedCard = new Card();
        activatedCard.setId(1L);
        activatedCard.setStatus(CardStatus.ACTIVE);

        CardResponseDto responseDto = new CardResponseDto();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(activatedCard);
        when(cardMapper.toDto(activatedCard)).thenReturn(responseDto);

        CardResponseDto result = cardService.activateCard(1L);

        assertEquals(responseDto, result);
        assertEquals(CardStatus.ACTIVE, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCardNotFoundForActivate() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.activateCard(1L));
    }

    @Test
    void shouldDeleteCardSuccessfully() {
        Card card = new Card();
        card.setId(1L);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        cardService.deleteCard(1L);

        verify(cardRepository).deleteById(1L);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCardNotFoundForDelete() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.deleteCard(1L));
    }

    @Test
    void shouldGetAllCardsWithoutUsername() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> cardsPage = new PageImpl<>(List.of(new Card()));
        when(cardRepository.findAll(pageable)).thenReturn(cardsPage);

        CardResponseDto responseDto = new CardResponseDto();
        when(cardMapper.toDto(any(Card.class))).thenReturn(responseDto);

        Page<CardResponseDto> result = cardService.getAllCards(pageable, null);

        assertEquals(1, result.getContent().size());
        verify(cardRepository).findAll(pageable);
    }

    @Test
    void shouldGetAllCardsWithUsername() {
        Pageable pageable = PageRequest.of(0, 10);
        String username = "testuser";

        User user = new User();
        user.setId(1L);

        Page<Card> cardsPage = new PageImpl<>(List.of(new Card()));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(cardRepository.findAllByUserId(pageable, 1L)).thenReturn(cardsPage);

        CardResponseDto responseDto = new CardResponseDto();
        when(cardMapper.toDto(any(Card.class))).thenReturn(responseDto);

        Page<CardResponseDto> result = cardService.getAllCards(pageable, username);

        assertEquals(1, result.getContent().size());
        verify(cardRepository).findAllByUserId(pageable, 1L);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForGetAllCardsWithUsername() {
        Pageable pageable = PageRequest.of(0, 10);
        String username = "unknownuser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.getAllCards(pageable, username));
    }

    @Test
    void shouldGetOwnCardsWithoutStatus() {
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setId(1L);

        Page<Card> cardsPage = new PageImpl<>(List.of(new Card()));

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findAllByUserId(pageable, 1L)).thenReturn(cardsPage);

        CardResponseDto responseDto = new CardResponseDto();
        when(cardMapper.toDto(any(Card.class))).thenReturn(responseDto);

        Page<CardResponseDto> result = cardService.getOwnCards(pageable, null);

        assertEquals(1, result.getContent().size());
        verify(cardRepository).findAllByUserId(pageable, 1L);
    }

    @Test
    void shouldGetOwnCardsWithStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        String cardStatus = "ACTIVE";

        User user = new User();
        user.setId(1L);

        Page<Card> cardsPage = new PageImpl<>(List.of(new Card()));

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(cardRepository.findAllByUserIdAndStatus(pageable, 1L, CardStatus.ACTIVE)).thenReturn(cardsPage);

        CardResponseDto responseDto = new CardResponseDto();
        when(cardMapper.toDto(any(Card.class))).thenReturn(responseDto);

        Page<CardResponseDto> result = cardService.getOwnCards(pageable, cardStatus);

        assertEquals(1, result.getContent().size());
        verify(cardRepository).findAllByUserIdAndStatus(pageable, 1L, CardStatus.ACTIVE);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForGetOwnCards() {
        Pageable pageable = PageRequest.of(0, 10);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("unknownuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("unknownuser")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cardService.getOwnCards(pageable, null));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenStatusIsInvalidForGetOwnCards() {
        Pageable pageable = PageRequest.of(0, 10);
        String cardStatus = "INVALID";

        User user = new User();
        user.setId(1L);

        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> cardService.getOwnCards(pageable, cardStatus));
    }
}