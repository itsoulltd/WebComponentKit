package com.infoworks.lab.jwtoken.services;

import com.infoworks.lab.cryptor.definition.KeyGenerator;

import java.security.Key;

public class AccessTokenProvider extends JWTokenProvider {

    private KeyGenerator keyGenerator;
    private String password;

    public AccessTokenProvider(String secret, String password, KeyGenerator keyGenerator) {
        super(secret);
        this.password = password;
        this.keyGenerator = keyGenerator;
    }

    public AccessTokenProvider(String secret, String password) {
        this(secret, password, null);
    }

    private String getPassword() {
        return this.password;
    }

    public KeyGenerator getKeyGenerator() {
        return this.keyGenerator;
    }

    public Key generateKey(String...args){
        String secret = (args.length > 0) ? args[0] : "";
        String keyString = (getKeyGenerator() != null)
                ? getKeyGenerator().generatePassword(secret + getPassword()
                                                                , secret)
                : getPassword();
        return super.generateKey(secret, keyString);
    }
}
