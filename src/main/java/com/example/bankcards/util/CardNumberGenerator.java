package com.example.bankcards.util;

import java.security.SecureRandom;

public class CardNumberGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }
}
