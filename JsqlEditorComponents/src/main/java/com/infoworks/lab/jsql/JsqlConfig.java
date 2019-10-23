package com.infoworks.lab.jsql;

import com.it.soul.lab.connect.JDBConnectionPool;
import com.it.soul.lab.jpql.service.JPQLExecutor;
import com.it.soul.lab.jpql.service.ORMController;
import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.SQLExecutor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.infoworks.lab.jsql.DataSourceKey.Keys;

public class JsqlConfig {

    private Set<String> configuredKeys = new ConcurrentSkipListSet<>();

    public JsqlConfig() {}

    private synchronized void config(String key, DataSourceKey container){
        try {
            if (key == null || key.isEmpty()){
                key = UUID.randomUUID().toString();
            }
            if (!configuredKeys.contains(key)){
                JDBConnectionPool.configure(key, createDataSource(container));
                configuredKeys.add(key);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private DataSourceKey getDefaultKeys() {
        DataSourceKey container = new DataSourceKey();
        container.set(Keys.URL, Keys.URL.defaultValue());
        container.set(Keys.DRIVER, Keys.DRIVER.defaultValue());
        container.set(Keys.SCHEMA, Keys.SCHEMA.defaultValue());
        container.set(Keys.USERNAME, Keys.USERNAME.defaultValue());
        container.set(Keys.PASSWORD, Keys.PASSWORD.defaultValue());
        container.set(Keys.HOST, Keys.HOST.defaultValue());
        container.set(Keys.PORT, Keys.PORT.defaultValue());
        container.set(Keys.NAME, Keys.NAME.defaultValue());
        container.set(Keys.QUERY, Keys.USERNAME.defaultValue());
        return container;
    }

    public Connection pullConnection(String key, DataSourceKey container){
        Connection connection = null;
        try {
            config(key, container);
            connection = JDBConnectionPool.connection(key);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    public Connection pullConnection(String key){
        DataSourceKey container = getDefaultKeys();
        return pullConnection(key, container);
    }

    public QueryExecutor create(ExecutorType type, String key, DataSourceKey container) {
        if (type == ExecutorType.SQL) {
            Connection connection = pullConnection(key, container);
            return new SQLExecutor(connection);
        }else if (type == ExecutorType.JPQL){
            ORMController controller = new ORMController(key);
            return new JPQLExecutor(controller.getEntityManager());
        }
        return null;
    }

    public QueryExecutor create(ExecutorType type, String key) {
        if (type == ExecutorType.SQL){
            DataSourceKey container = getDefaultKeys();
            return create(type, key, container);
        }else if (type == ExecutorType.JPQL){
            return create(type, key, null);
        }
        return null;
    }

    public DataSource createDataSource(DataSourceKey container) throws SQLException{

        String username = container.get(Keys.USERNAME);
        if(username==null) username = Keys.USERNAME.defaultValue();

        String password = container.get(Keys.PASSWORD);
        if (password==null) password = Keys.PASSWORD.defaultValue();

        String driverClassName = container.get(Keys.DRIVER);
        if (driverClassName==null) driverClassName = Keys.DRIVER.defaultValue();

        String url = container.get(Keys.URL);
        if (url==null){

            String schema = container.get(Keys.SCHEMA);
            if (schema==null) schema = Keys.SCHEMA.defaultValue();

            String name = container.get(Keys.NAME);
            if (name==null) throw new SQLException("'app.db.name' must not be null");

            String host = container.get(Keys.HOST);
            if (host==null) host = Keys.HOST.defaultValue();

            String port = container.get(Keys.PORT);
            if (port==null) port = Keys.PORT.defaultValue();

            String queryParam = container.get(Keys.QUERY);
            if (queryParam==null) queryParam = Keys.QUERY.defaultValue();

            url = String.format("%s%s:%s/%s%s"
                    , schema
                    , host
                    , port
                    , name
                    , queryParam);
        }
        System.out.println("DATA-SOURCE URL: " + url);
        //
        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setInitialSize(5);
        ds.setMaxActive(10);
        ds.setMaxIdle(5);
        ds.setMinIdle(2);
        //ds.setValidationQuery("select now()");
        return ds;
    }
}
