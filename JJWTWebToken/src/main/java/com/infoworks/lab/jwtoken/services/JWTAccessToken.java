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

    private String getPassword() {
        return this.password;
    }

    public SecretGenerator getSecretGenerator() {
        return this.secretGenerator;
    }

    protected Key generateKey(){
        String passPhrase = getPassword() + getSecret();
        String keyString = getSecretGenerator().generateSecurePassword(passPhrase, getSecret());
        Key key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, getSigAlgo().name());
        return key;
    }
}
