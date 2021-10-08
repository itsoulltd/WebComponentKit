package com.infoworks.lab.cryptor.util;

public enum CryptoAlgorithm {

    AES("AES",256),
    DES("DES", 56),
    DESede("DESede", 112),
    TripleDES("TripleDES", 168),
    RSA("RSA", 128);

    private String description;
    private int length;

    CryptoAlgorithm(String description, int length) {
        this.description = description;
        this.length = length;
    }

    @Override
    public String toString() {
        return "CryptoAlgorithm{" +
                "description='" + description + '\'' +
                ", length=" + length +
                '}';
    }

    public int length(){
        return length;
    }
}
