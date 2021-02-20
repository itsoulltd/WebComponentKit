package com.infoworks.lab.jwtoken.services;

import com.infoworks.lab.jwtoken.definition.SecretGenerator;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class JWTAccessToken extends JWToken{

    private SecretGenerator secretGenerator;
    private String password;

    public JWTAccessToken(String secret, String password, SecretGenerator secretGenerator) {
        super(secret);
        this.password = password;
        this.secretGenerator = secretGenerator;
    }

    public JWTAccessToken(String secret, String password) {
        this(secret, password, null);
    }

    private String getPassword() {
        return this.password;
    }

    public SecretGenerator getSecretGenerator() {
        return this.secretGenerator;
    }

    public Key generateKey(){
        String passPhrase = getSecret() + getPassword();
        String keyString = (getSecretGenerator() != null)
                ? getSecretGenerator().generateSecurePassword(passPhrase, getSecret())
                : passPhrase;
        Key key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, getSigAlgo().name());
        return key;
    }
}
