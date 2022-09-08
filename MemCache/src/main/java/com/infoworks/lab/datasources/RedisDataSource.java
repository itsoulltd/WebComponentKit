package com.infoworks.lab.datasources;

import com.it.soul.lab.data.base.DataSource;

import java.util.Map;

public interface RedisDataSource extends DataSource<String, Map<String, Object>>, AutoCloseable {
    void put(String key, Map<String, Object> entity, long ttl);
    void setTimeToLive(long ttl);
    long getTimeToLive();
}
