package com.example.bankcards.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class CardEncryptor {
    private final String algorithm;
    private final String key;

    public CardEncryptor(
            @Value("${encryption.algorithm}") String algorithm,
            @Value("${encryption.key}") String key
    ) {
        this.algorithm = algorithm;
        this.key = key;
    }

    public String encrypt(String data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    public String decrypt(String encryptedData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
    }
}
