package com.infoworks.lab.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Ref: https://stackoverflow.com/questions/1391918/does-java-have-a-linkedconcurrenthashmap-data-structure
 * @param <K>
 * @param <V>
 */
public class LinkedConcurrentHashMap<K, V> {

    private LinkedHashMap<K, V> linkedHashMap = null;
    private final int cacheSize;
    private ReadWriteLock readWriteLock = null;

    public LinkedConcurrentHashMap(LinkedHashMap<K, V> psCacheMap, int size) {
        this.linkedHashMap  = psCacheMap;
        cacheSize = size;
        readWriteLock = new ReentrantReadWriteLock();
    }

    public LinkedConcurrentHashMap(int initCapacity, float loadFactor, boolean accessOrder, int size) {
        this(new LinkedHashMap(initCapacity, loadFactor, accessOrder), size);
    }

    public void put(K key, V value) {
        Lock writeLock=readWriteLock.writeLock();
        try{
            writeLock.lock();
            if(linkedHashMap.size() >= cacheSize && cacheSize > 0){
                K oldAgedKey = linkedHashMap.keySet().iterator().next();
                remove(oldAgedKey);
            }
            linkedHashMap.put(key, value);
        }finally{
            writeLock.unlock();
        }
    }

    public V get(K key) {
        Lock readLock=readWriteLock.readLock();
        try{
            readLock.lock();
            return linkedHashMap.get(key);
        }finally{
            readLock.unlock();
        }
    }

    public boolean containsKey(K key) {
        Lock readLock=readWriteLock.readLock();
        try{
            readLock.lock();
            return linkedHashMap.containsKey(key);
        }finally{
            readLock.unlock();
        }
    }

    public V remove(K key) {
        Lock writeLock=readWriteLock.writeLock();
        try{
            writeLock.lock();
            return linkedHashMap.remove(key);
        }finally{
            writeLock.unlock();
        }
    }

    public ReadWriteLock getLock() {
        return readWriteLock;
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return linkedHashMap.entrySet();
    }

}
