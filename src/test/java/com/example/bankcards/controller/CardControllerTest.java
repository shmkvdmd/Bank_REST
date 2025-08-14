package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CardCreateRequestDto;
import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.service.card.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        this.mockMvc = MockMvcBuilders.standaloneSetup(cardController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void shouldCreateCardSuccessfully() throws Exception {
        CardCreateRequestDto requestDto = new CardCreateRequestDto();
        requestDto.setUserId(1L);
        requestDto.setInitialBalance(new BigDecimal("0.00"));
        requestDto.setExpirationDate(LocalDate.now().plusYears(4));

        CardResponseDto responseDto = new CardResponseDto(
                1L,
                "**** **** **** 1234",
                "user",
                BigDecimal.ZERO,
                requestDto.getExpirationDate(),
                null
        );

        when(cardService.createCard(any(CardCreateRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetCardSuccessfully() throws Exception {
        CardResponseDto responseDto = new CardResponseDto();

        when(cardService.getCardById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/cards/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetCardBalanceSuccessfully() throws Exception {
        CardResponseDto cardDto = new CardResponseDto();
        cardDto.setBalance(BigDecimal.valueOf(100));

        when(cardService.getCardById(1L)).thenReturn(cardDto);

        mockMvc.perform(get("/api/cards/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(100));
    }

    @Test
    void shouldBlockCardSuccessfully() throws Exception {
        CardResponseDto responseDto = new CardResponseDto();

        when(cardService.blockCard(1L)).thenReturn(responseDto);

        mockMvc.perform(patch("/api/cards/1/block"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldActivateCardSuccessfully() throws Exception {
        CardResponseDto responseDto = new CardResponseDto();

        when(cardService.activateCard(1L)).thenReturn(responseDto);

        mockMvc.perform(patch("/api/cards/1/activate"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteCardSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/cards/1"))
                .andExpect(status().isNoContent());
    }
}
