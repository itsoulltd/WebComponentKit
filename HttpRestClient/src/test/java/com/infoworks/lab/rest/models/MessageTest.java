package com.infoworks.lab.rest.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.rest.models.events.Event;
import com.infoworks.lab.rest.models.events.EventType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MessageTest {

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void payloadTest(){
        Message<Event> message = new Message<>();
        Event event = new Event().setEventType(EventType.START)
                .setTimestamp("1234556")
                .setUuid(UUID.randomUUID().toString());
        message.setEvent(event);
        //
        String json = message.toString();
        System.out.println(json);
        //
        try {
            Message<Event> nMessage = Message.getJsonSerializer().readValue(json, Message.class);
            Event event1 = nMessage.getEvent();
            Assert.assertTrue(Objects.equals(event, event1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void mapConversionWithDefault() throws IOException, ParseException {
        Map<String, Object> data = new HashMap<>();
        data.put("X-Auth-Token", "token-by-token");
        data.put("Status", 200);
        data.put("Datetime", format.parse("2021-12-31 23:01:00"));
        String json = Message.marshal(data);
        System.out.println(json);
        //
        Map<String, Object> reconstructed = Message.unmarshal(Map.class, json);
        Assert.assertEquals(reconstructed.get("X-Auth-Token"), "token-by-token");
        Assert.assertEquals(reconstructed.get("Status"), 200);
        Assert.assertEquals(new Date(Long.parseLong(reconstructed.get("Datetime").toString()))
                , format.parse("2021-12-31 23:01:00"));
    }

    @Test
    public void mapConversionWithTypeReference() throws IOException, ParseException {
        Map<String, Object> data = new HashMap<>();
        data.put("X-Auth-Token", "token-by-token");
        data.put("Status", 200);
        data.put("Datetime", format.parse("2021-12-31 23:01:00"));
        String json = Message.marshal(data);
        System.out.println(json);
        //
        Map<String, String> reconstructed = Message.unmarshal(new TypeReference<Map<String, String>>() {}, json);
        Assert.assertEquals(reconstructed.get("X-Auth-Token"), "token-by-token");
        Assert.assertEquals(reconstructed.get("Status"), "200");
        Assert.assertEquals(reconstructed.get("Datetime"), "1640988060000");
    }

}