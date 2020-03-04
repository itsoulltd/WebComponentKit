package com.infoworks.lab.jjwt;

public interface TokenValidation {
    Boolean isValid(String token, String... args);
    String getIssuer(String token, String... args);
    String getUserID(String token, String... args);
    String getTenantID(String token, String... args);
    static <T extends TokenValidation> T createValidator(Class<T> type) throws IllegalAccessException, InstantiationException{
        return type.newInstance();
    }
    static String parseToken(String token, String prefix) {
        if (token.trim().startsWith(prefix)) {
            String pToken = token.trim();
            return pToken.substring(prefix.length());
        } else {
            return token;
        }
    }
}
