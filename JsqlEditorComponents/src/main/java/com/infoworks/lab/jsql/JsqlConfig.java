package com.infoworks.lab.jsql;

import com.it.soul.lab.connect.JDBConnectionPool;
import com.it.soul.lab.jpql.service.JPQLExecutor;
import com.it.soul.lab.jpql.service.ORMController;
import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.SQLExecutor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.infoworks.lab.jsql.DataSourceKeyContainer.Keys;

public class JsqlConfig {

    private Set<String> configuredKeys = new ConcurrentSkipListSet<>();

    public JsqlConfig() {}

    private synchronized void config(String key, DataSourceKeyContainer container){
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

    private DataSourceKeyContainer getDataSourceKeyContainer() {
        DataSourceKeyContainer container = new DataSourceKeyContainer();
        container.set(Keys.URL, "app.db.url");
        container.set(Keys.DRIVER, "app.db.driver-class-name");
        container.set(Keys.SCHEMA, "app.db.schema");
        container.set(Keys.USERNAME, "app.db.username");
        container.set(Keys.PASSWORD, "app.db.password");
        container.set(Keys.HOST, "app.db.host");
        container.set(Keys.PORT, "app.db.port");
        container.set(Keys.NAME, "app.db.name");
        container.set(Keys.QUERY, "app.db.query");
        return container;
    }

    public Connection pullConnection(String key, DataSourceKeyContainer container){
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
        DataSourceKeyContainer container = getDataSourceKeyContainer();
        return pullConnection(key, container);
    }

    public QueryExecutor create(ExecutorType type, String key, DataSourceKeyContainer container) {
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
            DataSourceKeyContainer container = getDataSourceKeyContainer();
            return create(type, key, container);
        }else if (type == ExecutorType.JPQL){
            return create(type, key, null);
        }
        return null;
    }

    public DataSource createDataSource(DataSourceKeyContainer container) throws SQLException{

        Map<String, String> env = System.getenv();

        String username = env.get(container.get(Keys.USERNAME));
        if(username==null) username = Keys.USERNAME.toString();

        String password = env.get(container.get(Keys.PASSWORD));
        if (password==null) password = Keys.PASSWORD.toString();

        String driverClassName = env.get(container.get(Keys.DRIVER));
        if (driverClassName==null) driverClassName = Keys.DRIVER.toString();

        String url = env.get(container.get(Keys.URL));
        if (url==null){

            String schema = env.get(container.get(Keys.SCHEMA));
            if (schema==null) schema = Keys.SCHEMA.toString();

            String name = env.get(container.get(Keys.NAME));
            if (name==null) throw new SQLException("'app.db.name' must not be null");

            String host = env.get(container.get(Keys.HOST));
            if (host==null) host = Keys.HOST.toString();

            String port = env.get(container.get(Keys.PORT));
            if (port==null) port = Keys.PORT.toString();

            String queryParam = env.get(container.get(Keys.QUERY));
            if (queryParam==null) queryParam = Keys.QUERY.toString();

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
