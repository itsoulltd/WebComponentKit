package com.infoworks.lab.webapp.config;

import com.infoworks.lab.domain.datasources.MemCache;
import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.jsql.ExecutorType;
import com.infoworks.lab.jsql.JsqlConfig;
import com.it.soul.lab.sql.SQLExecutor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;

@Configuration
public class BeanConfig {

    @Bean("HelloBean")
    public String getHello(){
        return "Hi Spring Hello";
    }

    @Bean
    JsqlConfig getJsqlConfig(){
        return new JsqlConfig();
    }

    @Autowired
    private Environment env;

    @Bean("JDBConnectionPool")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
    SQLExecutor executor() {
        SQLExecutor exe = (SQLExecutor) getJsqlConfig().create(ExecutorType.SQL, appDBNameKey());
        System.out.println("Created DB Connections.");
        return exe;
    }

    @Bean("AppDBNameKey")
    String appDBNameKey(){
        return env.getProperty("app.db.name");
    }

    @Bean
    RedissonClient getRedisClient(){
        String redisHost = env.getProperty("app.redis.host") != null
                ? env.getProperty("app.redis.host") : "localhost";
        String redisPort = env.getProperty("app.redis.port") != null
                ? env.getProperty("app.redis.port") : "6379";
        Config conf = new Config();
        conf.useSingleServer()
                .setAddress(String.format("redis://%s:%s",redisHost, redisPort))
                .setRetryAttempts(5)
                .setRetryInterval(1500);
        //RedissionClient instance are fully-thread safe.
        return Redisson.create(conf);
    }

    @Bean("passengerCache")
    MemCache<Passenger> getPassengerCache(){
        return new MemCache<>(getRedisClient(), Passenger.class);
    }

}
