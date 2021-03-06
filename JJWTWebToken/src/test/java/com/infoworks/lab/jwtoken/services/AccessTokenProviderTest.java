package com.infoworks.lab.jwtoken.services;

import com.infoworks.lab.jjwt.JWTHeader;
import com.infoworks.lab.jjwt.JWTPayload;
import com.infoworks.lab.jjwt.JWTValidator;
import com.infoworks.lab.jwtoken.definition.TokenProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class AccessTokenProviderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createToken(){
        //
        JWTPayload payload = new JWTPayload().setSub("hi.there!")
                .setIss("towhid")
                .setIat(new Date().getTime())
                .setExp(TokenProvider.defaultTokenTimeToLive().getTimeInMillis())
                .addData("permission","yes")
                .addData("hasAccess","yes");
        //
        TokenProvider token = new AccessTokenProvider("em@evol@si@anahos", "123456")
                .setPayload(payload)
                .setHeader(new JWTHeader().setTyp("mytype").setKid("112223344"));
        //
        String tokenKey = token.generateToken(TokenProvider.defaultTokenTimeToLive());
        System.out.println(tokenKey);
        //
        boolean isTrue = token.isValid(tokenKey);
        Assert.assertTrue(isTrue);
        //
        JWTValidator validator = new JWTValidator();
        isTrue = validator.isValid(tokenKey, "em@evol@si@anahos", "123456");
        Assert.assertTrue(isTrue);
    }

    @Test
    public void refreshTest(){
        //
        JWTPayload payload = new JWTPayload().setSub("hi.there!")
                .setIss("towhid")
                .setIat(new Date().getTime())
                .setExp(TokenProvider.defaultTokenTimeToLive().getTimeInMillis())
                .addData("permission","yes")
                .addData("hasAccess","yes");
        //
        TokenProvider token = new AccessTokenProvider("em@evol@si@anahos", "123456")
                .setPayload(payload)
                .setHeader(new JWTHeader().setTyp("mytype").setKid("112223344"));
        //
        String actual = token.generateToken(TokenProvider.defaultTokenTimeToLive());
        System.out.println("actual: " + actual);
        //
        TokenProvider refresh = new AccessTokenProvider("em@evol@si@anahos", "123456");
        String refreshToken = refresh.refreshToken(actual, TokenProvider.defaultTokenTimeToLive());
        System.out.println("expected: " + refreshToken);
        Assert.assertNotEquals(refreshToken, actual);
        //
        JWTValidator validator = new JWTValidator();
        boolean isTrue = validator.isValid(refreshToken, "em@evol@si@anahos", "123456");
        Assert.assertTrue(isTrue);
    }

}