package com.infoworks.lab.jwtoken.services;

import com.infoworks.lab.jjwt.JWTHeader;
import com.infoworks.lab.jjwt.JWTPayload;
import com.infoworks.lab.jjwt.JWTValidator;
import com.infoworks.lab.jwtoken.definition.TokenProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

public class JWTokenProviderTest {

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
        TokenProvider token = new JWTokenProvider("ym@evol@si@anahos")
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
        isTrue = validator.isValid(tokenKey, "ym@evol@si@anahos");
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
        TokenProvider token = new JWTokenProvider("ym@evol@si@anahos")
                .setPayload(payload)
                .setHeader(new JWTHeader().setTyp("mytype").setKid("112223344"));
        //
        String actual = token.generateToken(TokenProvider.defaultTokenTimeToLive());
        System.out.println("actual: " + actual);
        //
        TokenProvider refresh = new JWTokenProvider("ym@evol@si@anahos");
        String refreshToken = refresh.refreshToken(actual, TokenProvider.defaultTokenTimeToLive());
        System.out.println("expected: " + refreshToken);
        Assert.assertNotEquals(refreshToken, actual);
        //
        JWTValidator validator = new JWTValidator();
        boolean isTrue = validator.isValid(refreshToken, "ym@evol@si@anahos");
        Assert.assertTrue(isTrue);
    }

    @Test
    public void expiredTest() {
        Calendar calendar = Calendar.getInstance();//Locale.getDefault()
        calendar.add(Calendar.MINUTE, -1);
        System.out.println("InMillis: " + calendar.getTimeInMillis());
        System.out.println("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
        //
        JWTPayload payload = new JWTPayload().setSub("hi.there!")
                .setIss("towhid")
                .setIat(new Date().getTime())
                .setExp(calendar.getTimeInMillis())
                .addData("permission","yes")
                .addData("hasAccess","yes");
        //
        TokenProvider token = new JWTokenProvider("ym@evol@si@anahos")
                .setPayload(payload)
                .setHeader(new JWTHeader().setTyp("mytype").setKid("112223344"));
        //
        String actual = token.generateToken(calendar);
        System.out.println("actual: " + actual);
        //
        JWTValidator validator = new JWTValidator();
        boolean isValid = validator.isValid(actual, "ym@evol@si@anahos");
        Assert.assertFalse(isValid);
    }

    @Test
    public void notExpiredTest() {
        Calendar calendar = Calendar.getInstance();//Locale.getDefault()
        calendar.add(Calendar.MINUTE, 1);
        System.out.println("InMillis: " + calendar.getTimeInMillis());
        System.out.println("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
        //
        JWTPayload payload = new JWTPayload().setSub("hi.there!")
                .setIss("towhid")
                .setIat(new Date().getTime())
                .setExp(calendar.getTimeInMillis())
                .addData("permission","yes")
                .addData("hasAccess","yes");
        //
        TokenProvider token = new JWTokenProvider("ym@evol@si@anahos")
                .setPayload(payload)
                .setHeader(new JWTHeader().setTyp("mytype").setKid("112223344"));
        //
        String actual = token.generateToken(calendar);
        System.out.println("actual: " + actual);
        //
        JWTValidator validator = new JWTValidator();
        boolean isValid = validator.isValid(actual, "ym@evol@si@anahos");
        Assert.assertTrue(isValid);
    }

    @Test
    public void expiredEventuallyTest() {
        Calendar calendar = Calendar.getInstance();//Locale.getDefault()
        calendar.add(Calendar.SECOND, 30);
        System.out.println("Token ttl for 30 sec");
        System.out.println("InMillis: " + calendar.getTimeInMillis());
        System.out.println("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
        //
        JWTPayload payload = new JWTPayload().setSub("hi.there!")
                .setIss("towhid")
                .setIat(new Date().getTime())
                .setExp(calendar.getTimeInMillis())
                .addData("permission","yes")
                .addData("hasAccess","yes");
        //
        TokenProvider token = new JWTokenProvider("ym@evol@si@anahos")
                .setPayload(payload)
                .setHeader(new JWTHeader().setTyp("mytype").setKid("112223344"));
        //
        String actual = token.generateToken(calendar);
        System.out.println("actual: " + actual);
        //Lets pause for 40 seconds:
        try {
            System.out.println("Going to sleep for 40 sec");
            System.out.println("Sleeping: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
            Thread.sleep(Duration.ofSeconds(40).toMillis());
            System.out.println("Back after: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
        } catch (InterruptedException e) {}
        //
        JWTValidator validator = new JWTValidator();
        boolean isValid = validator.isValid(actual, "ym@evol@si@anahos");
        Assert.assertFalse(isValid);
        System.out.println("token has expire: " + !isValid);
    }

    @Test(expected = RuntimeException.class)
    public void invalidSecretTest() {
        Calendar calendar = Calendar.getInstance();//Locale.getDefault()
        calendar.add(Calendar.MINUTE, -1);
        System.out.println("InMillis: " + calendar.getTimeInMillis());
        System.out.println("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
        //
        JWTPayload payload = new JWTPayload().setSub("hi.there!")
                .setIss("towhid")
                .setIat(new Date().getTime())
                .setExp(calendar.getTimeInMillis())
                .addData("permission","yes")
                .addData("hasAccess","yes");
        //
        TokenProvider token = new JWTokenProvider("ym@evol@si@anahos")
                .setPayload(payload)
                .setHeader(new JWTHeader().setTyp("mytype").setKid("112223344"));
        //
        String actual = token.generateToken(calendar);
        System.out.println("actual: " + actual);
        //
        JWTValidator validator = new JWTValidator();
        boolean isValid = validator.isValid(actual, "@evl@si@anaho");
        Assert.assertFalse(isValid);
    }

}