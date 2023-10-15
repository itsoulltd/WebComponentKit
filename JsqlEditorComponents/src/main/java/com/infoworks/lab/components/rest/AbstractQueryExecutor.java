package com.infoworks.lab.components.rest;

import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.query.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class AbstractQueryExecutor implements QueryExecutor<SQLSelectQuery
        , SQLInsertQuery
        , SQLUpdateQuery
        , SQLDeleteQuery
        , SQLScalarQuery> {

    @Override
    public Object createBlob(String s) throws SQLException {
        return null;
    }

    @Override
    public Boolean executeDDLQuery(String s) throws SQLException {
        return null;
    }

    @Override
    public Integer[] executeUpdate(int i, List list) throws SQLException, IllegalArgumentException {
        return new Integer[0];
    }

    @Override
    public List executeCRUDQuery(String s, Class aClass) throws SQLException, IllegalAccessException, InstantiationException {
        return null;
    }

    @Override
    public List executeSelect(String s, Class aClass, Map map) throws SQLException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        return null;
    }

    @Override
    public Integer[] executeInsert(boolean b, int i, SQLInsertQuery i1, List list) throws SQLException, IllegalArgumentException {
        return new Integer[0];
    }

    @Override
    public Integer executeDelete(int i, SQLDeleteQuery query, List list) throws SQLException {
        return null;
    }

    @Override
    public Integer[] executeUpdate(int i, SQLUpdateQuery query, List list) throws SQLException, IllegalArgumentException {
        return new Integer[0];
    }

    @Override
    public void begin() throws SQLException {

    }

    @Override
    public void end() throws SQLException {

    }

    @Override
    public void abort() throws SQLException {

    }

    @Override
    public void close() throws Exception {

    }

}
