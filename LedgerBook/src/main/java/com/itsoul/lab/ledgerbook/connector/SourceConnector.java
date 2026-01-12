package com.itsoul.lab.ledgerbook.connector;

import com.it.soul.lab.sql.QueryExecutor;

import java.sql.Connection;
import java.sql.SQLException;

public interface SourceConnector {
    String driverClassName();
    String url();
    String username();
    String password();
    String schema();
    Connection getConnection() throws SQLException;
    QueryExecutor getExecutor() throws SQLException;
    boolean generateSchema();
}
