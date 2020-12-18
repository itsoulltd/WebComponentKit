package com.infoworks.lab.jwtoken.services;

import com.infoworks.lab.jwtoken.definition.SecretGenerator;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class JWTAccessToken extends JWToken{

    private SecretGenerator secretGenerator;
    private String password;
    private String salt;

    public JWTAccessToken(String secret, String password, SecretGenerator secretGenerator) {
        super(secret);
        this.password = password;
        this.secretGenerator = secretGenerator;
    }

    public JWToken setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public String getSalt() {
        return this.salt != null ? salt : getSecretGenerator().generateSalt(12);
    }

    private String getPassword() {
        return this.password;
    }

    public SecretGenerator getSecretGenerator() {
        return this.secretGenerator;
    }

    protected Key generateKey(){
        SecretGenerator gen = getSecretGenerator();
        String passPhrase = getPassword() + getSecret();
        String keyString = gen.generateSecurePassword(passPhrase, getSalt());
        Key key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, getSigAlgo().name());
        return key;
    }
}
