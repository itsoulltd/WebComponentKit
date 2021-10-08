package com.infoworks.lab.cryptor.impl;

import com.infoworks.lab.cryptor.definition.Cryptor;
import com.infoworks.lab.cryptor.util.Transformation;
import com.infoworks.lab.cryptor.util.CryptoAlgorithm;
import com.infoworks.lab.cryptor.util.HashKey;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
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

    private final HashKey hashKey;
    private final Transformation transformation;
    private final CryptoAlgorithm cryptoAlgorithm;

    public AESCryptor() {
        this(HashKey.SHA_256, Transformation.AES_ECB_PKCS5Padding, CryptoAlgorithm.AES);
    }

    public AESCryptor(HashKey hashKey, Transformation transformation, CryptoAlgorithm cryptoAlgorithm) {
        this.hashKey = hashKey;
        this.transformation = transformation;
        this.cryptoAlgorithm = cryptoAlgorithm;
    }

    public CryptoAlgorithm getAlgorithm() {return cryptoAlgorithm;}
    public Transformation getTransformation() {return transformation;}
    public HashKey getHashKey() {return hashKey;}

    private Cipher getCipher(String secret) throws Exception{
        if (cipher == null){
            SecretKey secretKey = getSecretKey(secret);
            cipher = Cipher.getInstance(transformation.value());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        }
        return cipher;
    }

    private Cipher getDecipher(String secret) throws Exception{
        if (decipher == null){
            SecretKey secretKey = getSecretKey(secret);
            decipher = Cipher.getInstance(transformation.value());
            decipher.init(Cipher.DECRYPT_MODE, secretKey);
        }
        return decipher;
    }

    @Override
    public SecretKey getSecretKey(String mykey)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //
        if (mykey == null || mykey.isEmpty())
            throw new UnsupportedEncodingException("SecretKey is null or empty!");
        //
        if (transformation == Transformation.AES_ECB_PKCS5Padding){
            byte[] key = mykey.getBytes("UTF-8");
            key = getSha(hashKey).digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec secretKey = new SecretKeySpec(key, cryptoAlgorithm.name());
            return secretKey;
        }
        else if (transformation == Transformation.AES_CBC_PKCS7Padding){
            throw new NoSuchAlgorithmException(transformation.value() + " not supported yet");
        }
        else if (transformation == Transformation.AES_GCM_NoPadding){
            throw new NoSuchAlgorithmException(transformation.value() + " not supported yet");
        }
        else {
            throw new NoSuchAlgorithmException(transformation.value() + " not supported yet");
        }
    }

    private MessageDigest getSha(HashKey hashKey) throws NoSuchAlgorithmException {
        if (sha == null){
            sha = MessageDigest.getInstance(hashKey.value());
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
