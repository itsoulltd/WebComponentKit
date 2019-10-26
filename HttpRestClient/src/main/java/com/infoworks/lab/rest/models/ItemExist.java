package com.infoworks.lab.rest.models;

public class ItemExist extends Response {

    private Boolean isExist = false;

    public Boolean isExist() {
        return isExist;
    }

    public void setIsExist(Boolean exist) {
        isExist = exist;
    }
}
