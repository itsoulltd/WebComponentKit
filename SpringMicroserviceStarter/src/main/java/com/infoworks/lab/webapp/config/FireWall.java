package com.infoworks.lab.webapp.config;

import com.infoworks.lab.jjwt.JWTValidator;
import com.infoworks.lab.jjwt.TokenValidation;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

@Configuration
@RequestFilter(openAccess = {"/*"}, secret = "****")
public class FireWall extends GenericFilterBean {

    private static final String TOKEN_PREFIX = "Bearer ";
    protected Logger log = Logger.getLogger(this.getClass().getName());
    private Class validatorType;

    public FireWall(){
        this(JWTValidator.class);
    }

    public <T extends TokenValidation> FireWall(Class<T> type){
        validatorType = type;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String token = getHeaderValue(request, HttpHeaders.AUTHORIZATION);

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.sendError(HttpServletResponse.SC_OK, "success");
            return;
        }

        if (isOpenAccessRequest(request)) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            if (!isTokenValid(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }

    protected String appSecret(){
        RequestFilter annotation = getClass().getAnnotation(RequestFilter.class);
        return (annotation != null) ? annotation.secret() : null;
    }

    protected boolean isTokenValid(String token) {
        if (token == null) return false;
        token = TokenValidation.parseToken(token, TOKEN_PREFIX);
        if (appSecret() != null){
            String secret = appSecret();
            try {
                TokenValidation validator = TokenValidation.createValidator(validatorType);
                return validator.isValid(token, secret);
            } catch (IllegalAccessException e) {
                log.info(e.getMessage());
            } catch (InstantiationException e) {
                log.info(e.getMessage());
            }
        }
        return false;
    }

    protected String removePatternFromEnd(String target, String pattern){
        if (target.endsWith(pattern)){
            return target.substring(0, target.length() - pattern.length());
        }
        return target;
    }

    protected boolean isOpenAccessRequest(HttpServletRequest request) {
        //
        final String fullPath = removePatternFromEnd(request.getRequestURI().toLowerCase(), "/");

        boolean any = Arrays.stream(getOpenPaths())
                .map(s -> removePatternFromEnd(s.toLowerCase(), "/"))
                .anyMatch(path -> {
                    if (path.endsWith("/*")) {
                        String nonStaricPath = removePatternFromEnd(path, "/*");
                        return fullPath.startsWith(nonStaricPath);
                    }
                    else { return fullPath.endsWith(path);}
                });
        return any;
    }

    private String[] openPaths;
    public String[] getOpenPaths() {
        if (openPaths == null){
            RequestFilter annotation = getClass().getAnnotation(RequestFilter.class);
            if (annotation != null){
                openPaths = annotation.openAccess();
            }else {
                openPaths = new String[0];
            }
        }
        return openPaths;
    }

    protected String getHeaderValue(HttpServletRequest request, String key){
        String value = request.getHeader(key);
        if (value == null){
            value = request.getParameter(key);
        }
        return value;
    }

}
