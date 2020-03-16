package com.infoworks.lab.rest.models.pagination;

import com.it.soul.lab.sql.entity.Entity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Map;

public class SortDescriptor extends Entity implements Externalizable {

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
