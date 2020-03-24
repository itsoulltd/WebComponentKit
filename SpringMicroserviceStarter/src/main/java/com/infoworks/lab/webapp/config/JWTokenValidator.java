package com.infoworks.lab.webapp.config;

import com.infoworks.lab.jjwt.JWTValidator;
import com.infoworks.lab.jjwt.TokenValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode=ScopedProxyMode.TARGET_CLASS)
public class JWTokenValidator extends JWTValidator {

    @Autowired
    private HttpServletRequest request;

    @Override
    public Boolean isValid(String token, String... args) {
        if (token == null || token.isEmpty())
            token = TokenValidation.parseToken(getHeaderValue(HttpHeaders.AUTHORIZATION), "Bearer ");
        return super.isValid(token, args);
    }

    @Override
    public String getIssuer(String token, String... args) {
        if (token == null || token.isEmpty())
            token = TokenValidation.parseToken(getHeaderValue(HttpHeaders.AUTHORIZATION), "Bearer ");
        return super.getIssuer(token, args);
    }

    protected String getHeaderValue(String key){
        String value = request.getHeader(key);
        if (value == null){
            value = request.getParameter(key);
        }
        return value;
    }

}
