package com.infoworks.lab.cryptor.util;

public enum HashKey {

    //SHA-1, SHA-128, SHA-256
    SHA_1("SHA-1"),
    SHA_128("SHA-128"),
    SHA_256("SHA-256");

    private String val;

    HashKey(String val) {
        this.val = val;
    }

    public String value(){
        return val;
    }
}
