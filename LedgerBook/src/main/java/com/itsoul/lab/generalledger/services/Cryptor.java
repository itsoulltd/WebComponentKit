package com.itsoul.lab.generalledger.services;

import org.jvnet.hk2.annotations.Contract;

import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@Contract
public interface Cryptor {
    SecretKeySpec getKeySpace(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException;
    String encrypt(String secret, String strToEncrypt);
    String decrypt(String secret, String strToDecrypt);
}
