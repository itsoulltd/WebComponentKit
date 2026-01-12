package com.itsoul.lab.ledgerbook.connector;

import com.infoworks.sql.executor.QueryExecutor;
import com.infoworks.sql.executor.SQLExecutor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class SQLDataSourceConnector implements SourceConnector{

    private DataSource dataSource;
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String schema;
    private boolean shouldGenerateSchema;

    public SQLDataSourceConnector(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (dataSource == null) throw new SQLException("DataSource must not be null!");
        return dataSource.getConnection();
    }

    @Override
    public QueryExecutor getExecutor() throws SQLException {
        return new SQLExecutor(getConnection());
    }

    public SQLDataSourceConnector url(String url) {
        this.url = url;
        return this;
    }

    public SQLDataSourceConnector username(String username) {
        this.username = username;
        return this;
    }

    public SQLDataSourceConnector password(String password) {
        this.password = password;
        return this;
    }

    public SQLDataSourceConnector schema(String schema) {
        this.schema = schema;
        return this;
    }

    public SQLDataSourceConnector skipSchemaGeneration(boolean skip){
        this.shouldGenerateSchema = !skip;
        return this;
    }

    public SQLDataSourceConnector driverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        return this;
    }

    @Override
    public boolean generateSchema() {
        return shouldGenerateSchema;
    }

    public String driverClassName() {
        return this.driverClassName;
    }

    public String url() {
        return this.url;
    }

    public String username() {
        return this.username;
    }

    public String password() {
        return this.password;
    }

    public String schema() {
        return schema;
    }
}
