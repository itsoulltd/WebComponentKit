package com.infoworks.lab.rest.models.events;

import com.it.soul.lab.sql.entity.Entity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

public class Event extends Entity implements Externalizable {

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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(marshallingToMap(true));
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        Map<String, Object> data = (Map<String, Object>) in.readObject();
        unmarshallingFromMap(data, true);
    }
}
