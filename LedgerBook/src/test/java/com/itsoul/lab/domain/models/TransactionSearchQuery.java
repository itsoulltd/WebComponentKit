package com.itsoul.lab.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.rest.models.events.EventType;

public class TransactionSearchQuery extends SearchQuery {

    @JsonIgnore private String uuid;
    @JsonIgnore private String timestamp;
    @JsonIgnore private EventType eventType;

    private Integer page = 0;
    private Integer size = 10;

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public TransactionSearchQuery setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    @Override
    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public TransactionSearchQuery setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public TransactionSearchQuery setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    @Override
    public Integer getPage() {
        return page;
    }

    @Override
    public void setPage(Integer page) {
        this.page = page;
    }

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public void setSize(Integer size) {
        this.size = size;
    }
}
