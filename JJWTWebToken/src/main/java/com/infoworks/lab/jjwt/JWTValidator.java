package com.infoworks.lab.jjwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.jwtoken.definition.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;

//We must not use advance Java8 features here: because compatibility of Android Client:

public class JWTValidator implements TokenValidator {

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
        token = TokenValidator.parseToken(token, "Bearer ");
        //LOG.info(token);
        String[] parts = token.split("\\.");
        //LOG.info("HEADER: " + new String(Base64.getDecoder().decode(parts[0])));
        //LOG.info("PAYLOAD: " + new String(Base64.getDecoder().decode(parts[1])));
        try {
            ObjectMapper mapper = TokenProvider.getJsonSerializer();
            JWTHeader header = mapper.readValue(new String(Base64.getDecoder().decode(parts[0])), JWTHeader.class);
            String secret = getSecret(header, args);
            byte[] bytes = this.validateSecret(secret);
            Key key = new SecretKeySpec(bytes, 0, bytes.length, header.getAlg());
            Jws<Claims> cl = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            Claims claims =  cl.getBody();
            LOG.info("JWT Claims: " + claims.toString());
            //BugFix: claim.getExpiration().getTime() return time in millis with extra 3 0's,
            //That's why divided by 1000l.
            //Tested with java-1.8 & 11:
            long exp = claims.getExpiration().getTime() / 1000l;
            if (new Date(exp).before(new Date())){
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String getIssuer(String token, String... args) {
        return TokenValidator.getPayloadValue("iss", token);
    }

    @Override
    public String getUserID(String token, String... args) {
        return TokenValidator.getPayloadValue("iss", token);
    }

    @Override
    public String getSubject(String token, String... args) {
        return TokenValidator.getPayloadValue("sub", token);
    }

}
