package com.infoworks.lab.jjwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.jwtoken.definition.TokenProvider;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

public interface TokenValidator {

    Boolean isValid(String token, String... args);
    String getIssuer(String token, String... args);
    String getUserID(String token, String... args);
    String getSubject(String token, String... args);

    static  <Header extends JWTHeader> Header parseHeader(String token, Class<Header> payloadClass)
            throws RuntimeException{
        if (token == null || token.isEmpty()) return null;
        String[] parts = token.split("\\.");
        if (parts.length > 1) {
            try {
                ObjectMapper mapper = TokenProvider.getJsonSerializer();
                Header header = mapper.readValue(new String(Base64.getDecoder().decode(parts[0])), payloadClass);
                return header;
            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    static  <Payload extends JWTPayload> Payload parsePayload(String token, Class<Payload> payloadClass)
     throws RuntimeException{
        String decodedValue = parsePayload(token);
        if (decodedValue != null){
            try {
                ObjectMapper mapper = TokenProvider.getJsonSerializer();
                Payload payload = mapper.readValue(decodedValue, payloadClass);
                return payload;
            } catch (IOException e) {throw new RuntimeException(e);}
        }
        return null;
    }

    static String parsePayload(String token) {
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

    static String getPayloadValue(String key, String token) throws RuntimeException{
        if (key == null || key.isEmpty()) return null;
        String decodedValue = parsePayload(token);
        if (decodedValue != null){
            try {
                ObjectMapper mapper = TokenProvider.getJsonSerializer();
                Map<String, String> payload = mapper.readValue(decodedValue
                        , new TypeReference<Map<String, String>>(){});
                return (payload != null) ? payload.get(key) : null;
            } catch (IOException e) {throw new RuntimeException(e);}
        }
        return null;
    }

    static String parseToken(String token, String prefix) {
        if (token.trim().startsWith(prefix)) {
            String pToken = token.trim();
            return pToken.substring(prefix.length());
        } else {
            return token;
        }
    }

    static <T extends TokenValidator> T createValidator(Class<T> type) throws IllegalAccessException, InstantiationException{
        return type.newInstance();
    }
}
