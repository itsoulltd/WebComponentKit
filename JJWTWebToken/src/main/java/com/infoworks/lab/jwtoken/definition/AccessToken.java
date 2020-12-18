package com.infoworks.lab.jwtoken.definition;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Calendar;

public interface AccessToken extends AutoCloseable{
	
	String generateToken(Calendar timeToLive) throws RuntimeException;
	String refreshToken(String token, Calendar timeToLive) throws RuntimeException;
	Boolean isValid(String token);
	void makeExpire() throws RuntimeException;
	void dispose();

	static Calendar defaultTokenTimeToLive(){
        /*By Default 1 hour (in production) token to live, if not provided.*/
        Calendar cal = Calendar.getInstance();
        int amount = 1  * 60 * 60;
        cal.add(Calendar.SECOND, amount);
        return cal;
    }

    static ObjectMapper getJsonSerializer() {
		ObjectMapper jsonSerializer = new ObjectMapper();
		jsonSerializer.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		jsonSerializer.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		jsonSerializer.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return jsonSerializer;
	}
}
