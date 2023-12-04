package com.infoworks.lab.beans.tasks.rest.aggregate;

import com.infoworks.lab.rest.models.Response;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AggregatedResponse<T extends Response> extends Response implements Iterable<T>, Iterator<T> {

    private Map<Integer, T> source;
    private Iterator<Integer> keySetIterator;
    private Integer last;

    public void add(T response) {
        if (source == null) {
            source = new ConcurrentHashMap<>();
        }
        last = response.hashCode();
        source.put(last, response);
    }

    @NotNull @Override
    public Iterator<T> iterator() {
        keySetIterator = source.keySet().iterator();
        return this;
    }

    @Override
    public boolean hasNext() {
        return keySetIterator.hasNext();
    }

    @Override
    public T next() {
        T item = source.get(keySetIterator.next());
        return item;
    }

    @Override
    public void remove() {
        if (last == null) return;
        synchronized (source) {
            source.remove(last);
            Iterator<Integer> now = source.keySet().iterator();
            do {
                last = now.next();
            } while (now.hasNext());
        }
    }
}
