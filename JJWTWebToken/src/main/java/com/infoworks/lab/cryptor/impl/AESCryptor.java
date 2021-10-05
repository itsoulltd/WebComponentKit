package com.infoworks.lab.cryptor.impl;

import com.infoworks.lab.cryptor.definition.Cryptor;
import com.infoworks.lab.cryptor.util.AESMode;
import com.infoworks.lab.cryptor.util.SecretKeyAlgo;
import com.infoworks.lab.cryptor.util.ShaKey;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class AESCryptor implements Cryptor {

    private Cipher cipher;
    private Cipher decipher;
    private MessageDigest sha;

    private final ShaKey shaKey;
    private final AESMode aesMode;
    private final SecretKeyAlgo secretKeyAlgo;

    public AESCryptor() {
        this(ShaKey.Sha_1, AESMode.AES_ECB_PKCS5Padding, SecretKeyAlgo.AES);
    }

    public AESCryptor(ShaKey shaKey, AESMode aesMode, SecretKeyAlgo secretKeyAlgo) {
        this.shaKey = shaKey;
        this.aesMode = aesMode;
        this.secretKeyAlgo = secretKeyAlgo;
    }

    private Cipher getCipher(String secret) throws Exception{
        if (cipher == null){
            SecretKeySpec secretKey = getKeySpace(secret);
            cipher = Cipher.getInstance(aesMode.value());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        }
        return cipher;
    }

    private Cipher getDecipher(String secret) throws Exception{
        if (decipher == null){
            SecretKeySpec secretKey = getKeySpace(secret);
            decipher = Cipher.getInstance(aesMode.value());
            decipher.init(Cipher.DECRYPT_MODE, secretKey);
        }
        return decipher;
    }

    @Override
    public SecretKeySpec getKeySpace(String mykey)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //
        if (mykey == null || mykey.isEmpty())
            throw new UnsupportedEncodingException("SecretKey is null or empty!");
        //
        byte[] key = mykey.getBytes("UTF-8");
        key = getSha(shaKey).digest(key);
        key = Arrays.copyOf(key, 16);
        SecretKeySpec secretKey = new SecretKeySpec(key, secretKeyAlgo.name());
        return secretKey;
    }

    private MessageDigest getSha(ShaKey shaKey) throws NoSuchAlgorithmException {
        if (sha == null){
            sha = MessageDigest.getInstance(shaKey.value());
        }
        return sha;
    }

    @Override
    public String encrypt(String secret, String strToEncrypt) {
        try {
            Cipher cipher = getCipher(secret);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    @Override
    public String decrypt(String secret, String strToDecrypt) {
        try {
            Cipher cipher = getDecipher(secret);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
