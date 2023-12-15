package com.infoworks.lab.client.data.rest;

import com.it.soul.lab.sql.entity.Entity;

import java.util.Map;

public class Links extends Entity {

    private Map<String, Object> self;
    private Map<String, Object> first;
    private Map<String, Object> prev;
    private Map<String, Object> last;
    private Map<String, Object> profile;
    private Map<String, Object> search;
    public Links() {}

    public Links(Map<String, Map<String, Object>> dataMap) {
        this.profile = dataMap.get("profile");
        this.search = dataMap.get("search");
        this.self = dataMap.get("self");
        this.first = dataMap.get("first");
        this.last = dataMap.get("last");
        this.prev = dataMap.get("prev");
    }

    public Map<String, Object> getSelf() {
        return self;
    }

    public void setSelf(Map<String, Object> self) {
        this.self = self;
    }

    public Map<String, Object> getProfile() {
        return profile;
    }

    public void setProfile(Map<String, Object> profile) {
        this.profile = profile;
    }

    public Map<String, Object> getSearch() {
        return search;
    }

    public void setSearch(Map<String, Object> search) {
        this.search = search;
    }

    public Map<String, Object> getFirst() {
        return first;
    }

    public void setFirst(Map<String, Object> first) {
        this.first = first;
    }

    public Map<String, Object> getPrev() {
        return prev;
    }

    public void setPrev(Map<String, Object> prev) {
        this.prev = prev;
    }

    public Map<String, Object> getLast() {
        return last;
    }

    public void setLast(Map<String, Object> last) {
        this.last = last;
    }
}
