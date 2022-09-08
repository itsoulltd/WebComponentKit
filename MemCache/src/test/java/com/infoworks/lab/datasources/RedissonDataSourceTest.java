package com.infoworks.lab.datasources;

import com.infoworks.lab.rest.models.Response;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.time.Duration;
import java.util.Map;

public class RedissonDataSourceTest {

    private RedissonClient client;

    @Before
    public void setUp() throws Exception {
        String redisHost = "localhost";
        String redisPort = "6379";
        Config conf = new Config();
        conf.useSingleServer()
                .setAddress(String.format("redis://%s:%s",redisHost, redisPort))
                .setRetryAttempts(5)
                .setRetryInterval(1500);
        //Redisson-Client instance are fully-thread safe.
        client = Redisson.create(conf);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void connectionTest() {
        long ttl = Duration.ofMillis(1000).toMillis();
        RedissonDataSource rdatasource = new RedissonDataSource(client, ttl);
        Assert.assertTrue(client.isShutdown() == false);
    }

    @Test
    public void saveTest() {
        long ttl = Duration.ofMillis(20).toMillis();
        RedissonDataSource rdatasource = new RedissonDataSource(client, ttl);
        //Save in Redis:
        Response response = new Response().setStatus(300).setMessage("Hi there!");
        rdatasource.put("message", response.marshallingToMap(true));
        //Check from Redis:
        boolean isExist = rdatasource.containsKey("message");
        Assert.assertTrue("Message Failed to Save.", isExist);
        //Read from Redis:
        Map<String, Object> msg = rdatasource.read("message");
        Response msgRes = new Response();
        msgRes.unmarshallingFromMap(msg, true);
        //
        Assert.assertTrue(response.getStatus().intValue() == msgRes.getStatus().intValue());
    }

    @Test
    public void emptyExist() {
        RedissonDataSource rdatasource = new RedissonDataSource(client);
        //Check from Redis:
        boolean isExist = rdatasource.containsKey("message");
        Assert.assertTrue("Object Did Exist.", !isExist);
    }

    @Test
    public void removeTest() {
        long ttl = Duration.ofMillis(100).toMillis();
        RedissonDataSource rdatasource = new RedissonDataSource(client, ttl);
        //Save in Redis:
        Response response = new Response().setStatus(300).setMessage("Hi there!");
        rdatasource.put("message", response.marshallingToMap(true));
        //Check from Redis:
        boolean isExist = rdatasource.containsKey("message");
        Assert.assertTrue("Message Failed to Save.", isExist);
        //Remove from Redis:
        rdatasource.remove("message");
        boolean removed = rdatasource.containsKey("message");
        Assert.assertTrue("Message Removed.", removed == false);
    }
}