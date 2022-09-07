package com.infoworks.lab.datasources;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    public boolean containsKey(String key) {
        return client.getKeys().countExists(key) > 0;
    }
}
