package com.infoworks.lab.rest.models;

import com.infoworks.lab.rest.models.events.Event;
import com.infoworks.lab.rest.models.events.EventType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.*;

public class MessageTest {

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

}