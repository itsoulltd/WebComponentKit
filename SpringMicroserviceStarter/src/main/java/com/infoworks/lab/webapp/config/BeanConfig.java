package com.infoworks.lab.webapp.config;

import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.jsql.ExecutorType;
import com.infoworks.lab.jsql.JsqlConfig;
import com.it.soul.lab.data.simple.SimpleDataSource;
import com.it.soul.lab.sql.SQLExecutor;
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

    @Bean("passengerDatasource")
    public SimpleDataSource<String, Passenger> getPassengerDatasource(){
        return new SimpleDataSource<>();
    }

}
