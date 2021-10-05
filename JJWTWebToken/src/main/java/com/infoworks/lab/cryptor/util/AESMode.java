package com.infoworks.lab.cryptor.util;

public enum AESMode {

    AES_GCM_NoPadding("AES/GCM/NoPadding"),
    AES_ECB_PKCS5Padding("AES/ECB/PKCS5Padding"),
    AES_CBC_PKCS7Padding("AES/CBC/PKCS7Padding");

    private String val;

    AESMode(String val) {
        this.val = val;
    }

    public String value(){
        return val;
    }
}
