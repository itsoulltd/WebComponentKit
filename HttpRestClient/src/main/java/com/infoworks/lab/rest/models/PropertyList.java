package com.infoworks.lab.rest.models;

import com.it.soul.lab.sql.query.models.Property;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PropertyList implements Iterable<Property>, Iterator<Property> {

    private List<Property> properties = new ArrayList<>();
    private Iterator<Property> keyIterator;
    private Property last;

    public PropertyList add(Property property) {
        properties.add(property);
        last = property;
        return this;
    }

    @Override
    public Iterator<Property> iterator() {
        keyIterator = properties.iterator();
        return this;
    }

    @Override
    public boolean hasNext() {
        return keyIterator.hasNext();
    }

    @Override
    public Property next() {
        Property item = keyIterator.next();
        return item;
    }

    @Override
    public void remove() {
        //throw new UnsupportedOperationException("Not Implemented Yet!");
        if (last == null) return;
        synchronized (this.properties) {
            this.properties.remove(last);
            Iterator<Property> now = this.properties.iterator();
            do {
                last = now.next();
            } while (now.hasNext());
        }
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        if (properties == null) return;
        this.properties = properties;
    }
}
