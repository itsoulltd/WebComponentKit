package com.infoworks.lab.datasources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.rest.models.Message;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.io.IOException;
import java.util.Map;

public class LettuceDataSource implements RedisDataSource {

    private RedisClient client;
    private long timeToLive = 0l;
    private StatefulRedisConnection<String, String> connection;

    public LettuceDataSource(RedisClient client) {
        this.client = client;
        this.connection = this.client.connect();
    }

    public LettuceDataSource(RedisClient client, long timeToLive) {
        this(client);
        this.timeToLive = timeToLive;
    }

    @Override
    public Map<String, Object> read(String key) {
        RedisCommands<String, String> cmd = connection.sync();
        String json = cmd.get(key);
        if (Message.isValidJson(json)) {
            if (Message.isValidJson(json)){
                try {
                    Map<String, Object> data = Message.unmarshal(new TypeReference<Map<String, Object>>() {}, json);
                    return data;
                } catch (IOException e) {}
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> remove(String key) {
        RedisCommands<String, String> cmd = connection.sync();
        //TODO:
        return null;
    }

    @Override
    public void put(String key, Map<String, Object> stringObjectMap) {
        put(key, stringObjectMap);
    }

    @Override
    public void put(String key, Map<String, Object> entity, long ttl) {
        RedisCommands<String, String> cmd = connection.sync();
        //TODO
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
    public boolean containsKey(String s) {
        RedisCommands<String, String> cmd = connection.sync();
        //TODO:
        return false;
    }

    @Override
    public void close() throws Exception {
        if (client != null){
            client.close();
            client = null;
        }
    }
}
