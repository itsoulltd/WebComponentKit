package com.infoworks.lab.jjwt;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Map;
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
        String secret = null;
        if (args.length >= 1) secret = args[0];
        if (secret == null || secret.isEmpty())
            throw new Exception("Secret must not null or empty");
        //
        return secret;
    }

    @Override
    public Boolean isValid(String token, String... args) {
        //
        token = TokenValidation.parseToken(token, "Bearer ");
        //LOG.info(token);
        String[] parts = token.split("\\.");
        //LOG.info("HEADER: " + new String(Base64.getDecoder().decode(parts[0])));
        //LOG.info("HEADER: " + new String(Base64.getDecoder().decode(parts[1])));
        try {
            ObjectMapper mapper = getJsonSerializer();
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
        return parsePayload(token, "iss");
    }

    @Override
    public String getUserID(String token, String... args) {
        return parsePayload(token, "iss");
    }

    @Override
    public String getTenantID(String token, String... args) {
        return parsePayload(token, "sub");
    }

    public String parsePayload(String token, String key){
        if (key == null || key.isEmpty()) return null;
        String decodedValue = parsePayloadFromToken(token);
        if (decodedValue != null){
            try {
                ObjectMapper mapper = getJsonSerializer();
                Map<String, String> payload = mapper.readValue(decodedValue
                        , new TypeReference<Map<String, String>>(){});
                return (payload != null) ? payload.get(key) : null;
            } catch (IOException e) {LOG.log(Level.WARNING, e.getMessage(), e);}
        }
        return null;
    }

    @Override
    public <Payload extends JWTPayload> Payload parsePayload(String token, Class<Payload> payloadClass) {
        String decodedValue = parsePayloadFromToken(token);
        if (decodedValue != null){
            try {
                ObjectMapper mapper = getJsonSerializer();
                Payload payload = mapper.readValue(decodedValue, payloadClass);
                return payload;
            } catch (IOException e) {LOG.log(Level.WARNING, e.getMessage(), e);}
        }
        return null;
    }

    private String parsePayloadFromToken(String token){
        if (token == null || token.isEmpty()) return null;
        String[] sections = token.split("\\.");
        if (sections.length > 2) {
            String payload64 = sections[1];
            byte[] decoded = Base64.getDecoder().decode(payload64);
            String decodedValue = new String(decoded);
            if (decodedValue != null && decodedValue.startsWith("{")) {
                return decodedValue;
            }
        }
        return null;
    }

    protected ObjectMapper getJsonSerializer() {
        ObjectMapper jsonSerializer = new ObjectMapper();
        jsonSerializer.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonSerializer.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        jsonSerializer.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return jsonSerializer;
    }

}
