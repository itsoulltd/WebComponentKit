package com.infoworks.lab.jjwt;

import com.fasterxml.jackson.core.type.TypeReference;
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

    private Logger LOG = Logger.getLogger(this.getClass().getSimpleName());

    protected byte[] validateSecret(String secret) throws Exception{
        if (secret == null) throw new Exception("Secret Can't be null!!!");
        return secret.getBytes();
    }

    protected Key generateKey(String secret) throws Exception {
        byte[] bytes = validateSecret(secret);
        Key key = new SecretKeySpec(bytes, 0, bytes.length, "DES");
        return key;
    }

    public Boolean isValid(String token, String...args){
        Boolean result;
        try {
            String secret = null;
            if (args.length >= 1) secret = args[0];
            if (secret == null || secret.isEmpty())
                throw new Exception("Secret is null or empty");
            //
            Key key = generateKey(secret);
            Jws<Claims> cl = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            String issuerID = null;
            if (args.length >= 2) issuerID = args[1];
            if(issuerID != null) {
                String issuer = cl.getBody().getIssuer();
                result = issuerID.equalsIgnoreCase(issuer);
            }else {
                result = true;
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
            result = false;
        }
        return result;
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

    protected String parsePayload(String token, String key){
        if (token == null || token.isEmpty()) return null;
        if (key == null || key.isEmpty()) return null;
        String[] sections = token.split("\\.");
        if (sections.length > 2){
            String payload64 = sections[1];
            byte[] decoded = Base64.getDecoder().decode(payload64);
            String decodedValue = new String(decoded);
            if (decodedValue != null && decodedValue.startsWith("{")){
                try {
                    ObjectMapper mapper = getJsonSerializer();
                    Map<String, String> payload = mapper.readValue(decodedValue
                            , new TypeReference<Map<String, String>>(){});
                    return (payload != null) ? payload.get(key) : null;
                } catch (IOException e) {}
            }
        }
        return null;
    }

    private ObjectMapper getJsonSerializer() {
        ObjectMapper jsonSerializer = new ObjectMapper();
        //jsonSerializer.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //jsonSerializer.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        //jsonSerializer.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return jsonSerializer;
    }
}
