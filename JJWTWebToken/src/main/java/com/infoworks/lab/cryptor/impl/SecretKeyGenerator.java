package com.infoworks.lab.cryptor.impl;

import com.infoworks.lab.cryptor.definition.KeyGenerator;
import com.infoworks.lab.cryptor.util.CryptoAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class SecretKeyGenerator implements KeyGenerator {

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    @Override
    public CryptoAlgorithm getKeyAlgorithm() {
        return CryptoAlgorithm.DES;
    }

    @Override
    public String generatePassword(String password, String salt) {
        byte[] securePassword = hash(password.toCharArray(), salt.getBytes());
        return Base64.getEncoder().encodeToString(securePassword);
    }

    @Override
    public SecretKey generateKey() throws NoSuchAlgorithmException {
        javax.crypto.KeyGenerator secretKeyGenerator = javax.crypto.KeyGenerator.getInstance(getKeyAlgorithm().name());
        secretKeyGenerator.init(getKeyAlgorithm().length());
        SecretKey secret = secretKeyGenerator.generateKey();
        return secret;
    }

    public byte[] encrypt(String securePassword, String accessTokenMaterial) {
        return hash(securePassword.toCharArray(), accessTokenMaterial.getBytes());
    }

    public byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey secret = skf.generateSecret(spec);
            return secret.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }
}
