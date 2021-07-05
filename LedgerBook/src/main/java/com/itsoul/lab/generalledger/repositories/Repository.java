package com.itsoul.lab.generalledger.repositories;

import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.QueryTransaction;

import java.sql.SQLException;

public interface Repository extends QueryTransaction {

    QueryExecutor getQueryExecutor();
    void setQueryExecutor(QueryExecutor queryExecutor);

    @Override
    default void begin() throws SQLException {
        getQueryExecutor().begin();
    }

    @Override
    default void end() throws SQLException{
        getQueryExecutor().end();
    }

    @Override
    default void abort() throws SQLException{
        getQueryExecutor().abort();
    }

    @Override
    default void close() throws Exception{
        getQueryExecutor().close();
    }
}
