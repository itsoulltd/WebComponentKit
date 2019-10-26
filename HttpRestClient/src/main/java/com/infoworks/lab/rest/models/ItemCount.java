package com.infoworks.lab.rest.models;

public class ItemCount extends Response {

    private Long count = 0L;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
