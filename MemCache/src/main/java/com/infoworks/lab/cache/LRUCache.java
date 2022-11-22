package com.infoworks.lab.cache;

import com.it.soul.lab.data.base.DataSource;
import com.it.soul.lab.sql.entity.Entity;

import java.util.*;

/**
 * Eviction Policy: Least Recently Used.
 * Concurrency: Multiple Thread Should Perform operation on the container.
 * @param <E>
 */
public class LRUCache<E extends Entity> implements DataSource<String, E> {

    private final Deque<E> cacheStorage = new LinkedList<>();
    private final Set<String> keySet = new HashSet<>();
    private final int maxSize;

    public LRUCache(int maxSize) {
        this.maxSize = maxSize;
    }

    protected Collection<E> getCacheStorage() {
        return cacheStorage;
    }

    public List<E> fetch(int offset, int page) {
        if (page > getCacheStorage().size() || page <= 0) page = getCacheStorage().size();
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

    public void clear(){
        if (getCacheStorage().size() > 0){
            getCacheStorage().clear();
        }
    }

    @Override
    public void put(String key, E e) {
        synchronized (cacheStorage) {
            //super.put(key, e);
            //TODO:
        }
    }

    @Override
    public E remove(String key) {
        synchronized (cacheStorage) {
            //return super.remove(key);
            //TODO:
            return null;
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
