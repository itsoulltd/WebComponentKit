package com.infoworks.lab.components.rest;

import com.infoworks.lab.jsql.DataSourceKey;
import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.builder.AbstractQueryBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class RestExecutor implements QueryExecutor {

    private DataSourceKey sourceKey;

    public RestExecutor(DataSourceKey sourceKey) {
        this.sourceKey = sourceKey;
    }

    @Override
    public AbstractQueryBuilder createQueryBuilder(QueryType queryType) {
        return null;
    }

    @Override
    public Object createBlob(String s) throws SQLException {
        return null;
    }

    @Override
    public Boolean executeDDLQuery(String s) throws SQLException {
        return null;
    }

    @Override
    public Integer executeUpdate(SQLQuery query) throws SQLException {
        return null;
    }

    @Override
    public Integer[] executeUpdate(int i, List list) throws SQLException, IllegalArgumentException {
        return new Integer[0];
    }

    @Override
    public Integer executeDelete(SQLQuery query) throws SQLException {
        return null;
    }

    @Override
    public Integer executeInsert(boolean b, SQLQuery query) throws SQLException, IllegalArgumentException {
        return null;
    }

    @Override
    public Integer getScalarValue(SQLQuery query) throws SQLException {
        return null;
    }

    @Override
    public List executeCRUDQuery(String s, Class aClass) throws SQLException, IllegalAccessException, InstantiationException {
        return null;
    }

    @Override
    public List executeSelect(SQLQuery query, Class aClass, Map map) throws SQLException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        return null;
    }

    @Override
    public List executeSelect(String s, Class aClass, Map map) throws SQLException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        return null;
    }

    @Override
    public Integer[] executeInsert(boolean b, int i, SQLQuery i1, List list) throws SQLException, IllegalArgumentException {
        return new Integer[0];
    }

    @Override
    public Integer executeDelete(int i, SQLQuery query, List list) throws SQLException {
        return null;
    }

    @Override
    public Integer[] executeUpdate(int i, SQLQuery query, List list) throws SQLException, IllegalArgumentException {
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
