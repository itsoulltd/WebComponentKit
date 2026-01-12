package com.itsoul.lab.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.infoworks.sql.query.pagination.SearchQuery;

public class TransactionSearchQuery extends SearchQuery {

    @JsonIgnore private String uuid;
    @JsonIgnore private String timestamp;

    private Integer page = 0;
    private Integer size = 10;

    public String getUuid() {
        return uuid;
    }

    public TransactionSearchQuery setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public TransactionSearchQuery setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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
