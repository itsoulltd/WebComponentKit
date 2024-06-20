package com.infoworks.lab.datasources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.rest.models.Message;
import io.lettuce.core.RedisClient;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisKeyCommands;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LettuceDataSource implements RedisDataSource {

    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;
    private long timeToLive = 0l;

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
        if (Message.isValidJson(json)){
            try {
                Map<String, Object> data = Message.unmarshal(new TypeReference<Map<String, Object>>() {}, json);
                return data;
            } catch (IOException e) {}
        }
        return null;
    }

    @Override
    public Map<String, Object> remove(String key) {
        RedisCommands<String, String> cmd = connection.sync();
        long id = cmd.del(key);
        return null;
    }

    @Override
    public void put(String key, Map<String, Object> stringObjectMap) {
        put(key, stringObjectMap, timeToLive);
    }

    @Override
    public void put(String key, Map<String, Object> entity, long ttl) {
        RedisCommands<String, String> cmd = connection.sync();
        try {
            if (ttl > 0l) cmd.set(key, Message.marshal(entity), SetArgs.Builder.ex(ttl));
            else cmd.set(key, Message.marshal(entity));
        } catch (IOException e) {}
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
        RedisCommands<String, String> cmd = connection.sync();
        long id = cmd.exists(key);
        return id == 1l;
    }

    @Override
    public void close() throws Exception {
        if (client != null && connection != null){
            connection.close();
            connection = null;
            client.shutdown(50l, 70l, TimeUnit.MILLISECONDS);
            client = null;
        }
    }

    @Override
    public boolean isConnectionOpen() {
        if (client != null && connection != null) return connection.isOpen();
        return false;
    }

    @Override
    public String[] keys(String prefix) {
        //TODO (CAUTION): fetch cmd.keys(pattern) in Batch (in-future) to avoid memory-dumb.
        RedisKeyCommands<String, String> cmd = connection.sync();
        List<String> keys = cmd.keys(prefix);
        List<String> targetMaskedKeys = keys.stream()
                .filter(key -> key.startsWith(prefix))
                .collect(Collectors.toList());
        targetMaskedKeys.sort(String::compareTo);
        return targetMaskedKeys.toArray(new String[0]);
    }
}
