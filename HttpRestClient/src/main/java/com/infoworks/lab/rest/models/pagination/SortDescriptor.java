package com.infoworks.lab.rest.models.pagination;

import java.util.List;

public class SortDescriptor {
    private SortOrder order;
    private List<String> keys;

    public SortDescriptor(SortOrder order) {
        this.order = order;
    }

    public SortDescriptor() {
        this(SortOrder.ASE);
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }
}
