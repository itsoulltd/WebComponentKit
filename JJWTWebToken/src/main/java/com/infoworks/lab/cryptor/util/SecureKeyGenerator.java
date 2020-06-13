package com.infoworks.lab.cryptor.util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

public class SecureKeyGenerator {
    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    
    public String generateUUID() {
    	return UUID.randomUUID().toString();
    }
    
    public String generateSecureUUID() throws NoSuchAlgorithmException, UnsupportedEncodingException {
    	MessageDigest salt = MessageDigest.getInstance("SHA-256");
    	salt.update(UUID.randomUUID().toString().getBytes("UTF-16"));
    	String digest = new String(salt.digest());
    	return digest;
    }
    
    public String generateSalt(int length) {
        StringBuilder returnValue = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(returnValue);
    }
    
    public String generateUserId(int length){
        return generateSalt(length);
    }
    
    public String generateSecurePassword(String password, String salt) throws InvalidKeySpecException {
        byte[] securePassword = hash(password.toCharArray(), salt.getBytes());
        return Base64.getEncoder().encodeToString(securePassword);
    }
    
    public SecretKey generateSecretKey(SecretKeyAlgo algo) throws NoSuchAlgorithmException {
        KeyGenerator secretKeyGenerator = KeyGenerator.getInstance(algo.name());
        secretKeyGenerator.init(algo.length());
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
