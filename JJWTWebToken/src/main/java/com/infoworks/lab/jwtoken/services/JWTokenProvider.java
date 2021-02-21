package com.infoworks.lab.jwtoken.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infoworks.lab.jjwt.JWTHeader;
import com.infoworks.lab.jjwt.JWTPayload;
import com.infoworks.lab.jjwt.TokenValidator;
import com.infoworks.lab.jwtoken.definition.TokenProvider;
import com.it.soul.lab.sql.entity.EntityInterface;
import io.jsonwebtoken.*;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public class JWTokenProvider implements TokenProvider {

    protected Logger LOG = Logger.getLogger(this.getClass().getSimpleName());

    private String secret;
    private JWTHeader header;
    private JWTPayload payload;
    private SignatureAlgorithm sigAlgo = SignatureAlgorithm.HS512;


    public JWTokenProvider(String secret) {
        this.secret = secret;
    }

    protected SignatureAlgorithm getSigAlgo() {
        return sigAlgo;
    }

    public JWTokenProvider setSigAlgo(SignatureAlgorithm sigAlgo) {
        this.sigAlgo = sigAlgo;
        return this;
    }

    public JWTokenProvider setHeader(JWTHeader header) {
        this.header = header;
        return this;
    }

    public JWTokenProvider setPayload(JWTPayload payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public String generateToken(Calendar timeToLive) throws RuntimeException {
        try {
            String jwtToken = generateJWToken(getSigAlgo()
                    , timeToLive);
            return jwtToken;
        } catch (Exception e) {
            LOG.warning(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String refreshToken(String token, Calendar timeToLive) throws RuntimeException {
        try {
            //Check if token already created, then check the expiration date etc etc.
            if(token == null) {
                return generateToken(timeToLive);
            }else{
                if (getHeader() == null){
                    setHeader(TokenValidator.parseHeader(token, JWTHeader.class));
                }
                if (getPayload() == null){
                    JWTPayload payload = TokenValidator.parsePayload(token, JWTPayload.class);
                    payload.setExp(timeToLive.getTimeInMillis());
                    setPayload(payload);
                }
                String jwtToken = generateJWToken(getSigAlgo()
                        , timeToLive);
                return jwtToken;
            }
        } catch (Exception e) {
            LOG.warning(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean isValid(String token) {
        try {
            Key key = generateKey(getSecret());
            Jws<Claims> cl = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            Claims claims =  cl.getBody();
            LOG.info("JWT Claims: " + claims.toString());
            return true;
        } catch (Exception e) {
            LOG.warning(e.getMessage());
        }
        return false;
    }

    @Override
    public void makeExpire() throws RuntimeException {
        //TODO:
    }

    @Override
    public void dispose() {
        //TODO:
    }

    @Override
    public void close() throws Exception {
        dispose();
    }

    private String getSecret() {
        return this.secret;
    }

    public Key generateKey(String...args){
        StringBuffer buffer = new StringBuffer();
        Arrays.stream(args).forEach(str -> buffer.append(str));
        String keyString = buffer.toString();
        Key key = new SecretKeySpec(keyString.getBytes()
                , 0
                , keyString.getBytes().length
                , getSigAlgo().name());
        return key;
    }

    protected final String generateJWToken(SignatureAlgorithm sig, Calendar timeToLive) {
        try {
            Key key = generateKey(getSecret());
            JwtBuilder builder = Jwts.builder()
                    .signWith(sig, key);
            //
            if(getHeader() != null) builder.setHeaderParams(getHeader().marshallingToMap(true));
            if(this.payload != null){
                if (this.payload.getIat() <= 0l){
                    this.payload.setIat(new Date().getTime());
                }
                if (this.payload.getExp() <= 0l){
                    this.payload.setExp(timeToLive.getTimeInMillis());
                }
                builder.setPayload(getPayload());
            }else {
                builder.setIssuedAt(new Date())
                        .setExpiration(timeToLive.getTime());
            }
            //
            return builder.compact();
        } catch (Exception e) {
            LOG.warning(e.getMessage());
        }
        return null;
    }

    private String getPayload() {
        if (this.payload != null){
            try {
                return TokenProvider.getJsonSerializer().writeValueAsString(this.payload);
            } catch (JsonProcessingException e) {
                LOG.warning(e.getMessage());
            }
        }
        return null;
    }

    private EntityInterface getHeader() {
        return this.header;
    }

}
