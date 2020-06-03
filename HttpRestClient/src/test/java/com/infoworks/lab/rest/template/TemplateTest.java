package com.infoworks.lab.rest.template;

import com.infoworks.lab.client.jersey.HttpTemplate;
import com.infoworks.lab.rest.models.Message;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.BreakIterator;
import java.util.Map;
import java.util.Objects;

public class TemplateTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void accessTokenTest(){
        HttpTemplate template = new HttpTemplate();
        //Case: 1.1
        AcessToken acessToken = new AcessToken();
        Map.Entry<String, Object> secureEntry = template.getSecureEntry(acessToken);
        Assert.assertTrue(Objects.nonNull(secureEntry.getValue()) == false);
        //Case: 1.2
        acessToken.setAccessToken("any-token");
        secureEntry = template.getSecureEntry(acessToken);
        Assert.assertTrue(Objects.nonNull(secureEntry.getValue()) == true);
    }

    @Test
    public void tokenTest(){
        HttpTemplate template = new HttpTemplate();
        //Case: 1.1
        Token acessToken = new Token();
        Map.Entry<String, Object> secureEntry = template.getSecureEntry(acessToken);
        Assert.assertTrue(Objects.nonNull(secureEntry.getValue()) == false);
        //Case: 1.2
        acessToken.setToken("any-token");
        secureEntry = template.getSecureEntry(acessToken);
        Assert.assertTrue(Objects.nonNull(secureEntry.getValue()) == true);
    }

    @Test
    public void bearerTokenTest(){
        HttpTemplate template = new HttpTemplate();
        //Case: 1.1
        BearerToken acessToken = new BearerToken();
        Map.Entry<String, Object> secureEntry = template.getSecureEntry(acessToken);
        Assert.assertTrue(Objects.nonNull(secureEntry.getValue()) == false);
        //Case: 1.2
        acessToken.setBearerToken("any-token");
        secureEntry = template.getSecureEntry(acessToken);
        Assert.assertTrue(Objects.nonNull(secureEntry.getValue()) == true);
    }

    @Test
    public void authorizationTest(){
        HttpTemplate template = new HttpTemplate();
        //Case: 1.1
        Authorization acessToken = new Authorization();
        Map.Entry<String, Object> secureEntry = template.getSecureEntry(acessToken);
        Assert.assertTrue(Objects.nonNull(secureEntry.getValue()) == false);
        //Case: 1.2
        acessToken.setAuthorization("any-token");
        secureEntry = template.getSecureEntry(acessToken);
        Assert.assertTrue(Objects.nonNull(secureEntry.getValue()) == true);
    }

    public static class AcessToken extends Message {
        private String accessToken; //or accesstoken

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }

    public static class Authorization  extends Message {
        private String authorization;

        public String getAuthorization() {
            return authorization;
        }

        public void setAuthorization(String authorization) {
            this.authorization = authorization;
        }
    }

    public static class Token extends Message {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class BearerToken extends Message {
        private String bearerToken; //or bearertoken

        public String getBearerToken() {
            return bearerToken;
        }

        public void setBearerToken(String bearerToken) {
            this.bearerToken = bearerToken;
        }
    }


}