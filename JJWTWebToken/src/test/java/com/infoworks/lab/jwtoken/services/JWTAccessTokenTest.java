package com.infoworks.lab.jwtoken.services;

import com.infoworks.lab.cryptor.util.SecretKeyAlgo;
import com.infoworks.lab.jjwt.JWTHeader;
import com.infoworks.lab.jjwt.JWTPayload;
import com.infoworks.lab.jjwt.JWTValidator;
import com.infoworks.lab.jwtoken.definition.AccessToken;
import com.infoworks.lab.jwtoken.definition.SecretGenerator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

public class JWTAccessTokenTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createToken(){
        //
        JWTPayload payload = new JWTPayload().setSub("hi.there!")
                .setIss("towhid")
                .setIat(new Date().getTime())
                .setExp(AccessToken.defaultTokenTimeToLive().getTimeInMillis())
                .addData("permission","yes")
                .addData("hasAccess","yes");
        //
        AccessToken token = new JWTAccessToken("em@evol@si@anahos", "123456")
                .setPayload(payload)
                .setHeader(new JWTHeader().setTyp("mytype").setKid("112223344"));
        //
        String tokenKey = token.generateToken(AccessToken.defaultTokenTimeToLive());
        System.out.println(tokenKey);
        //
        boolean isTrue = token.isValid(tokenKey);
        Assert.assertTrue(isTrue);
        //
        JWTValidator validator = new JWTValidator();
        isTrue = validator.isValid(tokenKey, "em@evol@si@anahos", "123456");
        Assert.assertTrue(isTrue);
    }

    @Test
    public void refreshTest(){
        //
        JWTPayload payload = new JWTPayload().setSub("hi.there!")
                .setIss("towhid")
                .setIat(new Date().getTime())
                .setExp(AccessToken.defaultTokenTimeToLive().getTimeInMillis())
                .addData("permission","yes")
                .addData("hasAccess","yes");
        //
        AccessToken token = new JWTAccessToken("em@evol@si@anahos", "123456")
                .setPayload(payload)
                .setHeader(new JWTHeader().setTyp("mytype").setKid("112223344"));
        //
        String actual = token.generateToken(AccessToken.defaultTokenTimeToLive());
        System.out.println("actual: " + actual);
        //
        AccessToken refresh = new JWTAccessToken("em@evol@si@anahos", "123456");
        String refreshToken = refresh.refreshToken(actual, AccessToken.defaultTokenTimeToLive());
        System.out.println("expected: " + refreshToken);
        Assert.assertNotEquals(refreshToken, actual);
        //
        JWTValidator validator = new JWTValidator();
        boolean isTrue = validator.isValid(refreshToken, "em@evol@si@anahos", "123456");
        Assert.assertTrue(isTrue);
    }

    public static class SecretGen implements SecretGenerator {

        private static final int ITERATIONS = 10000;
        private static final int KEY_LENGTH = 256;

        @Override
        public SecretKeyAlgo getSecretKeyAlgo() {
            return SecretKeyAlgo.DES;
        }

        @Override
        public String generateSecurePassword(String password, String salt) {
            byte[] securePassword = hash(password.toCharArray(), salt.getBytes());
            return Base64.getEncoder().encodeToString(securePassword);
        }

        @Override
        public SecretKey generateSecretKey() throws NoSuchAlgorithmException {
            KeyGenerator secretKeyGenerator = KeyGenerator.getInstance(getSecretKeyAlgo().name());
            secretKeyGenerator.init(getSecretKeyAlgo().length());
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
}