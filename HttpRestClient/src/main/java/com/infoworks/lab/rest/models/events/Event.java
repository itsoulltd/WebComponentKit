package com.infoworks.lab.rest.models.events;

import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.entity.Entity;

public class Event extends Entity {

    public static <P extends Response> P CreateErrorResponse(Throwable exp, Class<P> type){
        Entity newInstance = null;
        try {
            newInstance = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            newInstance = new Response();
        }
        if (newInstance instanceof Event){
            ((Event)newInstance).setEventType(EventType.ERROR);
        }
        return (P) newInstance;
    }

    private String uuid;
    private String timestamp;
    private EventType eventType;

    public EventType getEventType() {
        return eventType;
    }

    public Event setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public String getUuid() {
        return uuid;
    }

    public Event setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Event setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

}
