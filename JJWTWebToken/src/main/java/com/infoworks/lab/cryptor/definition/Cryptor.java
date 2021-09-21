package com.infoworks.lab.cryptor.definition;

import com.infoworks.lab.cryptor.impl.AESCryptor;

import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface Cryptor {

    static Cryptor create(){return new AESCryptor();}

    SecretKeySpec getKeySpace(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException;
    String encrypt(String secret, String strToEncrypt);
    String decrypt(String secret, String strToDecrypt);
}
