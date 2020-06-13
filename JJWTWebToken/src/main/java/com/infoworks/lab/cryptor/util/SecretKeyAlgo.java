package com.infoworks.lab.cryptor.util;

public enum SecretKeyAlgo {

    AES("AES",256),
    DES("DES", 56),
    DESede("DESede", 112),
    TripleDES("TripleDES", 168);

    private String description;
    private int length;

    SecretKeyAlgo(String description, int length) {
        this.description = description;
        this.length = length;
    }

    @Override
    public String toString() {
        return "SecretKeyAlgo{" +
                "description='" + description + '\'' +
                ", length=" + length +
                '}';
    }

    public int length(){
        return length;
    }
}
