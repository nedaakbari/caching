package com.example.caching.service;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

@Service
public class PasswordService {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

    public byte[] getEncryptedPassword(String password, byte[] salt) {
        var derivedKeyLength = 160;
        var iterations = 20000;
        var spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);

        try {
            var secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            var secret = secretKeyFactory.generateSecret(spec);
            return secret.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getSalt() {
        var random = new SecureRandom();
        var salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
}
