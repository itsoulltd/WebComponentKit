package com.infoworks.lab.cryptor.util;

public enum ShaKey {

    //SHA-1, SHA-128, SHA-256
    Sha_1("SHA-1"),
    Sha_128("SHA-128"),
    Sha_256("SHA-256");

    private String val;

    ShaKey(String val) {
        this.val = val;
    }

    public String value(){
        return val;
    }
}
