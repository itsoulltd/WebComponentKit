package com.infoworks.lab.cache;

import com.it.soul.lab.data.base.DataSource;
import com.it.soul.lab.data.simple.SimpleDataSource;
import com.it.soul.lab.sql.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Eviction Policy: Least Recently Used.
 * Concurrency: Multiple Thread Should Perform operation on the container.
 * @param <E>
 */
public class CacheDataSource<E extends Entity> extends SimpleDataSource<String, E> {

    private final LRUCache<String, E> cacheStorage;

    public CacheDataSource(int maxSize) {
        this.cacheStorage = new LRUCache<>(maxSize);
    }

    protected Collection<E> getCacheStorage() {
        return cacheStorage.values();
    }

    @Override
    protected Map<String, E> getInMemoryStorage() {
        return cacheStorage;
    }

    @Override
    public int size() {
        return cacheStorage.size();
    }

    @Override
    public void clear(){
        if (size() > 0){
            cacheStorage.clear();
        }
    }

    public List<E> fetch(int offset, int page) {
        int size = size();
        if (page > size || page <= 0) page = size;
        return readSyncAsList(offset, page);
    }

    private List<E> readSyncAsList(int offset, int pageSize) {
        int size = size();
        int maxItemCount = Math.abs(offset) + Math.abs(pageSize);
        if (maxItemCount <= size) {
            List<E> values = new ArrayList<>(getCacheStorage());
            List<E> items = values.subList(Math.abs(offset), maxItemCount);
            return items;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void put(String key, E e) {
        synchronized (cacheStorage) {
            cacheStorage.put(key, e);
        }
    }

    @Override
    public E remove(String key) {
        synchronized (cacheStorage) {
            return cacheStorage.remove(key);
        }
    }

    @Override
    public void add(E e) {
        put(String.valueOf(e.hashCode()), e);
    }

    public DataSource<String, E> add(E...items){
        for (E dh: items) add(dh);
        return this;
    }

    @Override
    public void delete(E e) {
        remove(String.valueOf(e.hashCode()));
    }

    public DataSource<String, E> delete(E...items){
        for (E dh: items) delete(dh);
        return this;
    }

    @Override
    public boolean contains(E e) {
        return containsKey(String.valueOf(e.hashCode()));
    }

}
