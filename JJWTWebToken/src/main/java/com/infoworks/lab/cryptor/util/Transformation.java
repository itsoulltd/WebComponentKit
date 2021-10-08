package com.infoworks.lab.cryptor.util;

public enum Transformation {

    AES_GCM_NoPadding("AES/GCM/NoPadding"),
    AES_ECB_PKCS5Padding("AES/ECB/PKCS5Padding"),
    AES_CBC_PKCS7Padding("AES/CBC/PKCS7Padding"),
    RSA_ECB_PKCS1Padding("RSA/ECB/PKCS1Padding");

    private String val;

    Transformation(String val) {
        this.val = val;
    }

    public String value(){
        return val;
    }
}
