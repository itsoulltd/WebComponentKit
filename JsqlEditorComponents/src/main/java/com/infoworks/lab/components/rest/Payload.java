package com.infoworks.lab.components.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.it.soul.lab.sql.entity.Entity;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Payload extends Entity {

    private Map<String, Object> payload = new HashMap<>();

    public Payload() {/**/}

    public Payload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
