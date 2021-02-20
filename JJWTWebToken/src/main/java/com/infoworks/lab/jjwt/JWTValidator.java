package com.infoworks.lab.jjwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.jwtoken.definition.AccessToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

//We must not use advance Java8 features here: because compatibility of Android Client:

public class JWTValidator implements TokenValidation{

    protected Logger LOG = Logger.getLogger(this.getClass().getSimpleName());

    protected byte[] validateSecret(String secret) throws Exception{
        if (secret == null) throw new Exception("Secret Can't be null!!!");
        return secret.getBytes();
    }

    protected String getSecret(JWTHeader header, String...args) throws Exception{
        StringBuffer buffer = new StringBuffer();
        Arrays.stream(args).forEach(str -> buffer.append(str));
        String secret = buffer.toString();
        if (secret == null || secret.isEmpty())
            throw new Exception("Secret must not null or empty");
        return secret;
    }

    @Override
    public Boolean isValid(String token, String... args) {
        //
        token = TokenValidation.parseToken(token, "Bearer ");
        //LOG.info(token);
        String[] parts = token.split("\\.");
        //LOG.info("HEADER: " + new String(Base64.getDecoder().decode(parts[0])));
        //LOG.info("PAYLOAD: " + new String(Base64.getDecoder().decode(parts[1])));
        try {
            ObjectMapper mapper = AccessToken.getJsonSerializer();
            JWTHeader header = mapper.readValue(new String(Base64.getDecoder().decode(parts[0])), JWTHeader.class);
            String secret = getSecret(header, args);
            byte[] bytes = this.validateSecret(secret);
            Key key = new SecretKeySpec(bytes, 0, bytes.length, header.getAlg());
            Jws<Claims> cl = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            Claims claims =  cl.getBody();
            LOG.info("JWT Claims: " + claims.toString());
            return true;
        } catch (Exception e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
        }
        return false;
    }

    @Override
    public String getIssuer(String token, String... args) {
        return TokenValidation.getPayloadValue("iss", token);
    }

    @Override
    public String getUserID(String token, String... args) {
        return TokenValidation.getPayloadValue("iss", token);
    }

    @Override
    public String getSubject(String token, String... args) {
        return TokenValidation.getPayloadValue("sub", token);
    }

}
