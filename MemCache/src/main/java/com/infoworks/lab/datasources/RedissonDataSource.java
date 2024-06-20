package com.infoworks.lab.datasources;

import org.redisson.api.RKeys;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RedissonDataSource implements RedisDataSource {

    private RedissonClient client;
    private long timeToLive = 0l;

    public RedissonDataSource(RedissonClient client) {
        this.client = client;
    }

    public RedissonDataSource(RedissonClient client, long timeToLive) {
        this(client);
        this.timeToLive = timeToLive;
    }

    @Override
    public Map<String, Object> read(String key) {
        RMap rData = client.getMap(key);
        if (rData != null && rData.size() > 0) {
            Map<String, Object> data = new HashMap<>(rData.size());
            Iterator<Map.Entry<String, Object>> itr = rData.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<String, Object> entry = itr.next();
                data.put(entry.getKey(), entry.getValue());
            }
            return data;
        }
        return null;
    }

    @Override
    public Map<String, Object> remove(String key) {
        //Clear the cache:
        RMap rData = client.getMap(key);
        if (rData != null && rData.size() > 0){
            rData.clear();
        }
        return null;
    }

    @Override
    public void put(String key, Map<String, Object> rData) {
        put(key, rData, timeToLive);
    }

    @Override
    public void put(String key, Map<String, Object> rData, long ttl) {
        RMap sData = client.getMap(key);
        if (sData.size() > 0){
            sData.clear();
        }
        rData.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .forEach(entry -> sData.put(entry.getKey(), entry.getValue()));
        //We are adding time_to_live only if ttl is greater than 0l:
        if (ttl > 0l) sData.expire(ttl, TimeUnit.MILLISECONDS);
    }

    @Override
    public void setTimeToLive(long ttl) {
        this.timeToLive = ttl;
    }

    @Override
    public long getTimeToLive() {
        return timeToLive;
    }

    @Override
    public boolean containsKey(String key) {
        RKeys keys = client.getKeys();
        long id = keys.countExists(key);
        return id == 1l;
    }

    @Override
    public void close() throws Exception {
        if (client != null){
            client.shutdown(50l, 70l, TimeUnit.MILLISECONDS);
            client = null;
        }
    }

    @Override
    public boolean isConnectionOpen() {
        if (client != null) return client.isShutdown() == false;
        return false;
    }

    @Override
    public String[] keys(String prefix) {
        //TODO (CAUTION): fetch client.getKeys() in Batch (in-future) to avoid memory-dumb.
        RKeys keys = client.getKeys();
        List<String> targetMaskedKeys = keys.getKeysStream()
                .filter(key -> key.startsWith(prefix))
                .collect(Collectors.toList());
        targetMaskedKeys.sort(String::compareTo);
        return targetMaskedKeys.toArray(new String[0]);
    }
}
