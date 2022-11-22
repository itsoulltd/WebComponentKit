package com.infoworks.lab.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V>  extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = 1L;
    private final int lruSize;

    public LRUCache(int initSize, int maxSize) {
        super((initSize > maxSize ? Math.round(maxSize/2) : initSize)
                , 0.75f
                , true);
        this.lruSize = maxSize;
    }

    public LRUCache(int maxSize) {
        this(Math.round(maxSize/2), maxSize);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > lruSize;
    }

}
