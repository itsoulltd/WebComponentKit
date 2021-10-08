package com.infoworks.lab.cryptor.definition;

import com.infoworks.lab.cryptor.impl.AESCryptor;
import com.infoworks.lab.cryptor.util.CryptoAlgorithm;
import com.infoworks.lab.cryptor.util.HashKey;
import com.infoworks.lab.cryptor.util.Transformation;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface Cryptor {

    static Cryptor create(){return new AESCryptor();}

    SecretKey getSecretKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException;
    String encrypt(String secret, String strToEncrypt);
    String decrypt(String secret, String strToDecrypt);

    CryptoAlgorithm getAlgorithm();
    Transformation getTransformation();
    HashKey getHashKey();

}
