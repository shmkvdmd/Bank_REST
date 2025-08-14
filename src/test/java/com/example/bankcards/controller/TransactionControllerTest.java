package com.example.bankcards.controller;

import com.example.bankcards.dto.request.TransactionRequestDto;
import com.example.bankcards.dto.response.TransactionResponseDto;
import com.example.bankcards.service.transaction.TransactionService;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        MappingJackson2HttpMessageConverter jacksonConverter =
                new MappingJackson2HttpMessageConverter(objectMapper);

        this.mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(jacksonConverter)
                .build();
    }

    @Test
    void shouldCreateTransactionSuccessfully() throws Exception {
        TransactionRequestDto request = new TransactionRequestDto();
        request.setSenderCardId(10L);
        request.setReceiverCardId(11L);
        request.setAmount(new BigDecimal("150.00"));

        TransactionResponseDto responseDto = new TransactionResponseDto();
        responseDto.setId(1L);
        responseDto.setAmount(new BigDecimal("150.00"));
        responseDto.setStatus(null);
        responseDto.setCreatedAt(Instant.now());

        when(transactionService.createTransaction(any(TransactionRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("150")));
    }
}
