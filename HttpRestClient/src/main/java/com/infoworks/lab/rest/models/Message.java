package com.infoworks.lab.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.rest.models.events.Event;
import com.it.soul.lab.sql.entity.Entity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message<E extends Event> extends Entity implements Externalizable {

    public Message() {
        classType = (Class<E>) Event.class;
    }

    public Message(Class<E> classType) {
        //TODO:Test
        /*TypeVariable[] types = getClass().getTypeParameters();
        for (TypeVariable type : types) {
            System.out.println("Parameterized Type:" + type);
        }*/
        //
        this.classType = classType;
    }

    private String payload;

    public String getPayload() {
        return payload;
    }

    public Message setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    @JsonIgnore
    private E event;

    @JsonIgnore
    private Class<E> classType;

    public E getEvent() {
        return getEvent(classType);
    }

    public E getEvent(Class<E> type) {
        this.classType = type;
        if (event == null){
            try {
                event = unmarshalMessagePayload(type, getPayload());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return event;
    }

    public Message setEvent(E event) {
        this.event = event;
        try {
            setPayload(marshalMessagePayload(event));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    protected boolean isValidJson(String json){
        if (json != null && !json.isEmpty()) {
            return json.trim().startsWith("{") || json.trim().startsWith("[");
        }
        return false;
    }

    protected ObjectMapper getJsonSerializer(){
        ObjectMapper jsonSerializer = new ObjectMapper();
        jsonSerializer.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonSerializer.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        jsonSerializer.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return jsonSerializer;
    }

    protected  <P extends Object> P unmarshalMessagePayload(Class<P> type, String payload) throws IOException {
        if (isValidJson(payload)){
            final ObjectMapper mapper = getJsonSerializer();
            P obj = mapper.readValue(payload, type);
            return obj;
        }
        return null;
    }

    protected  <P extends Object> String marshalMessagePayload(P object) throws IOException {
        if (object != null){
            final ObjectMapper mapper = getJsonSerializer();
            String value = mapper.writeValueAsString(object);
            return value;
        }
        return null;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = getJsonSerializer();
        if (mapper != null){
            try {
                String json = mapper.writeValueAsString(this);
                return json;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return super.toString();
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
