package com.infoworks.lab.components.rest;

import com.it.soul.lab.sql.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class EntityPayload extends Entity {

    private Map<String, Object> payload = new HashMap<>();

    public EntityPayload() {/**/}

    public EntityPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
