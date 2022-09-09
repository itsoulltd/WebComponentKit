package com.infoworks.lab.datasources;

import com.infoworks.lab.PerformanceLogger;
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
        if (client != null)
            client.shutdown();
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
        PerformanceLogger logger = new PerformanceLogger();
        Response response = new Response().setStatus(300).setMessage("Hi there!");
        rdatasource.put("message", response.marshallingToMap(true));
        logger.printMillis("saveTest:put");
        //Check from Redis:
        logger = new PerformanceLogger();
        boolean isExist = rdatasource.containsKey("message");
        logger.printMillis("saveTest:contain");
        Assert.assertTrue("Message Failed to Save.", isExist);
        //Read from Redis:
        logger = new PerformanceLogger();
        Map<String, Object> msg = rdatasource.read("message");
        Response msgRes = new Response();
        msgRes.unmarshallingFromMap(msg, true);
        logger.printMillis("saveTest:read");
        //
        Assert.assertTrue(response.getStatus().intValue() == msgRes.getStatus().intValue());
    }

    @Test
    public void emptyExist() {
        RedissonDataSource rdatasource = new RedissonDataSource(client);
        //Check from Redis:
        PerformanceLogger logger = new PerformanceLogger();
        boolean isExist = rdatasource.containsKey("message");
        logger.printMillis("emptyExist:contain");
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
        PerformanceLogger logger = new PerformanceLogger();
        rdatasource.remove("message");
        logger.printMillis("removeTest:remove");
        //Checking:
        boolean removed = rdatasource.containsKey("message");
        Assert.assertTrue("Message Removed.", removed == false);
    }
}