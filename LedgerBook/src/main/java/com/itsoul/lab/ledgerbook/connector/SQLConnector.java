package com.itsoul.lab.ledgerbook.connector;

import com.it.soul.lab.connect.JDBConnection;
import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.SQLExecutor;

import java.sql.Connection;
import java.sql.SQLException;

import static com.itsoul.lab.ledgerbook.connector.SourceConfig.*;

/**
 *
 */
public final class SQLConnector implements SourceConnector {

    public static final SourceConnector EMBEDDED_H2_CONNECTOR = new SQLConnector(EMBEDDED_H2);

    public static final SourceConnector EMBEDDED_DERBY_CONNECTOR = new SQLConnector(EMBEDDED_DERBY);

    public static final SourceConnector EMBEDDED_HSQL_CONNECTOR = new SQLConnector(EMBEDDED_HSQL);

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String schema;
    private boolean shouldGenerateSchema;

    public SQLConnector(SourceConfig config) {
        this.driverClassName = config.getDriverClassName();
        this.url = config.getUrl();
        this.username = config.getUsername();
        this.password = config.getPassword();
        this.schema = config.getSchema();
    }

    public SQLConnector(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public SQLConnector url(String url) {
        this.url = url;
        return this;
    }

    public SQLConnector username(String username) {
        this.username = username;
        return this;
    }

    public SQLConnector password(String password) {
        this.password = password;
        return this;
    }

    public SQLConnector schema(String schema) {
        this.schema = schema;
        return this;
    }

    public SQLConnector skipSchemaGeneration(boolean skip){
        this.shouldGenerateSchema = !skip;
        return this;
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

    @Override
    public Connection getConnection() throws SQLException {
        return new JDBConnection.Builder(url())
                .credential(username(), password())
                .build();
    }

    @Override
    public QueryExecutor getExecutor() throws SQLException {
        return new SQLExecutor(getConnection());
    }

    @Override
    public boolean generateSchema() {
        return shouldGenerateSchema;
    }
}
