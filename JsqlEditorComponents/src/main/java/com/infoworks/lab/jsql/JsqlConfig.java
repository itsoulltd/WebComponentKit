package com.infoworks.lab.jsql;

import com.infoworks.lab.components.rest.RestExecutor;
import com.it.soul.lab.connect.DriverClass;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.infoworks.lab.jsql.DataSourceKey.Keys;

public class JsqlConfig {

    private Logger LOG = Logger.getLogger(this.getClass().getSimpleName());
    private DataSource dataSource;
    private Set<String> configuredKeys = new ConcurrentSkipListSet<>();

    public JsqlConfig() {}

    public JsqlConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private synchronized void config(String key, DataSourceKey container){
        if (key != null && configuredKeys.contains(key)) return;
        try {
            if (key == null || key.isEmpty()){
                key = UUID.randomUUID().toString();
            }
            JDBConnectionPool.configure(key, createDataSource(container));
            configuredKeys.add(key);
        } catch (SQLException e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public Connection pullConnection(String key, DataSourceKey container) throws SQLException{
        config(key, container);
        Connection connection = JDBConnectionPool.connection(key);
        return connection;
    }

    public Connection pullConnection(String key) throws SQLException{
        DataSourceKey container = DataSourceKey.createDataSourceKey(null);
        return pullConnection(key, container);
    }

    public QueryExecutor create(ExecutorType type, String key, DataSourceKey container) throws Exception{
        if (type == ExecutorType.SQL) {
            Connection connection = pullConnection(key, container);
            return new SQLExecutor(connection);
        }else if (type == ExecutorType.JPQL){
            ORMController controller = new ORMController(key);
            return new JPQLExecutor(controller.getEntityManager());
        }else if (type == ExecutorType.REST){
            return new RestExecutor(container);
        }
        return null;
    }

    public QueryExecutor create(ExecutorType type, String key) throws Exception{
        if (type == ExecutorType.SQL){
            DataSourceKey container = DataSourceKey.createDataSourceKey(null);
            return create(type, key, container);
        }else {
            return create(type, key, null);
        }
    }

    public DataSource createDataSource(DataSourceKey container) throws SQLException{
        //Return if passed in constructor:
        if (dataSource != null) return dataSource;

        //Else create a un-managed datasource:
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
        LOG.info("DATA-SOURCE URL: " + url);
        //
        DataSource ds = createDataSource(url, driverClassName, username, password);
        configureDataSource(ds);
        dataSource = ds;
        return ds;
    }

    protected DataSource createDataSource(String url, String driverClassName, String username, String password){
        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    protected void configureDataSource(DataSource ds){
        if (ds instanceof org.apache.tomcat.jdbc.pool.DataSource){
            org.apache.tomcat.jdbc.pool.DataSource poolDS = (org.apache.tomcat.jdbc.pool.DataSource) ds;
            poolDS.setInitialSize(5);
            poolDS.setMaxActive(10);
            poolDS.setMaxIdle(5);
            poolDS.setMinIdle(2);
            poolDS.setTimeBetweenEvictionRunsMillis(34*1000);
            poolDS.setMinEvictableIdleTimeMillis(55*1000);
            if(((org.apache.tomcat.jdbc.pool.DataSource) ds).getDriverClassName()
                    .equalsIgnoreCase(DriverClass.MYSQL.toString())) {
                poolDS.setValidationQuery("SELECT 1");//for MySqlDB
            }else if(((org.apache.tomcat.jdbc.pool.DataSource) ds).getDriverClassName()
                    .equalsIgnoreCase(DriverClass.OracleOCI9i.toString())){
                poolDS.setValidationQuery("SELECT 1 from dual"); //for OraclDB
            }
            poolDS.setValidationInterval(34*1000);
            poolDS.setTestOnBorrow(true);
            poolDS.setRemoveAbandoned(true);
            poolDS.setRemoveAbandonedTimeout(55);
            poolDS.setLoginTimeout(60*1000);
        }
    }

}
