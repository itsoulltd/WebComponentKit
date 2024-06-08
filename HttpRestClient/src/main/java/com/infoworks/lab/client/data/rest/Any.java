package com.infoworks.lab.client.data.rest;

import com.it.soul.lab.sql.entity.Entity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.Optional;

public class Any<ID> extends Entity implements Externalizable {
    private ID id;
    private Map<String, Object> _links;

    public Any() {}

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public Map<String, Object> get_links() {
        return _links;
    }

    public void set_links(Map<String, Object> _links) {
        this._links = _links;
    }

    public Optional<Object> parseId() {
        if (get_links() == null) return null;
        Map<String, Object> self = (Map<String, Object>) get_links().get("self");
        if (self != null) {
            String href = self.get("href").toString();
            String[] paths = href.split("/");
            Object last = paths[paths.length - 1];
            return Optional.ofNullable(last);
        }
        return Optional.ofNullable(null);
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
