package com.infoworks.lab.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.rest.models.events.Event;
import com.infoworks.lab.rest.models.pagination.SortOrder;
import com.it.soul.lab.sql.entity.Entity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message<E extends Event> extends Entity implements Externalizable, Comparable<Message> {

    public Message() {
        classType = (Class<E>) Event.class;
    }

    public Message(Class<E> classType) {
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

    @JsonIgnore
    public static boolean isValidJson(String json){
        if (json != null && !json.isEmpty()) {
            return json.trim().startsWith("{") || json.trim().startsWith("[");
        }
        return false;
    }

    @JsonIgnore
    public static ObjectMapper getJsonSerializer(){
        ObjectMapper jsonSerializer = new ObjectMapper();
        jsonSerializer.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonSerializer.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        jsonSerializer.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return jsonSerializer;
    }

    public static  <P extends Object> P unmarshal(Class<P> type, String payload) throws IOException {
        return internalUnmarshal(type, payload);
    }

    public static  <P extends Object> P unmarshal(TypeReference<P> type, String payload) throws IOException {
        return internalUnmarshal(type, payload);
    }

    private static  <P extends Object> P internalUnmarshal(Object type, String payload) throws IOException {
        if (isValidJson(payload) && type != null){
            final ObjectMapper mapper = getJsonSerializer();
            if (type instanceof TypeReference){
                P obj = mapper.readValue(payload, (TypeReference<P>) type);
                return obj;
            }else{
                P obj = mapper.readValue(payload, (Class<P>) type);
                return obj;
            }
        }
        return null;
    }

    public static   <P extends Object> String marshal(P object) throws IOException {
        if (object != null){
            final ObjectMapper mapper = getJsonSerializer();
            String value = mapper.writeValueAsString(object);
            return value;
        }
        return null;
    }

    public static int compareWithOrder(Message o1, Message o2, String sortBy, SortOrder order) {
        if (order == SortOrder.ASC)
            return compare(o1, o2, sortBy);
        else
            return compare(o2, o1, sortBy);
    }

    public static int compare(Message o1, Message o2, String sortBy) {
        return o1.compareTo(o2, sortBy);
    }

    @Override
    public int compareTo(Message other) {
        return compareTo(other, "payload");
    }

    public int compareTo(Message other, String sortBy) {
        Object obj1 = this.getSortBy(sortBy);
        Object obj2 = other.getSortBy(sortBy);
        if (obj1 != null && obj2 != null) {
            String value = obj1.toString();
            String oValue = obj2.toString();
            if (obj1 instanceof Integer && obj2 instanceof Integer) {
                return Integer.valueOf(value).compareTo(Integer.valueOf(oValue));
            } else if (obj1 instanceof Long && obj2 instanceof Long) {
                return Long.valueOf(value).compareTo(Long.valueOf(oValue));
            } else if (obj1 instanceof Float && obj2 instanceof Float) {
                return Float.valueOf(value).compareTo(Float.valueOf(oValue));
            } else if (obj1 instanceof Double && obj2 instanceof Double) {
                return Double.valueOf(value).compareTo(Double.valueOf(oValue));
            } else if (obj1 instanceof BigDecimal && obj2 instanceof BigDecimal) {
                return new BigDecimal(value).compareTo(new BigDecimal(oValue));
            } else if (obj1 instanceof Boolean && obj2 instanceof Boolean) {
                return Boolean.valueOf(value).compareTo(Boolean.valueOf(oValue));
            }else {
                return value.compareTo(oValue);
            }
        } else {
            return 0; //So that, list remain as is;
        }
    }

    protected final Object getSortBy(String sortBy) {
        if (sortByIsEmpty(sortBy)) return null;
        Field fl = null;
        try {
            fl = getClass().getDeclaredField(sortBy);
            fl.setAccessible(true);
            return fl.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } finally {
            if (fl != null) fl.setAccessible(false);
        }
        return null;
    }

    protected final boolean sortByIsEmpty(String sortBy) {
        return sortBy == null || sortBy.isEmpty();
    }

}
