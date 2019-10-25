package com.infoworks.lab.components.rest;

import com.infoworks.lab.client.jersey.HttpTemplate;
import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.jsql.DataSourceKey;
import com.infoworks.lab.rest.template.Interactor;
import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.query.*;
import com.it.soul.lab.sql.query.builder.AbstractQueryBuilder;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.infoworks.lab.jsql.DataSourceKey.Keys;

public class RestExecutor implements QueryExecutor<SQLSelectQuery
        , SQLInsertQuery
        , SQLUpdateQuery
        , SQLDeleteQuery
        , SQLScalarQuery> {

    private DataSourceKey sourceKey;

    public RestExecutor(DataSourceKey sourceKey) {
        this.sourceKey = sourceKey;
    }

    public DataSourceKey getSourceKey() {
        return sourceKey;
    }

    private URI parseURI(DataSourceKey sourceKey){
        URI uri = URI.create(String.format("%s%s:%s/%s/%s"
                , getSourceKey().get(Keys.SCHEMA)
                , getSourceKey().get(Keys.HOST)
                , getSourceKey().get(Keys.PORT)
                , getSourceKey().get(Keys.NAME)
                , (getSourceKey().get(Keys.QUERY) != null && !(getSourceKey().get(Keys.QUERY).isEmpty())
                        ? getSourceKey().get(Keys.QUERY) : "")));
        return uri;
    }

    @Override
    public AbstractQueryBuilder createQueryBuilder(QueryType queryType) {
        return new SQLQuery.Builder(queryType);
    }

    @Override
    public Integer getScalarValue(SQLScalarQuery query) throws SQLException {
        //TODO: Calls Come Here
        URI uri = parseURI(getSourceKey());
        try (HttpTemplate template = Interactor.create(HttpTemplate.class, uri)){
            template.get(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (HttpInvocationException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List executeSelect(SQLSelectQuery query, Class aClass, Map map) throws SQLException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        //TODO: Calls Come Here
        return null;
    }

    @Override
    public Integer executeInsert(boolean b, SQLInsertQuery query) throws SQLException, IllegalArgumentException {
        //TODO: Calls Come Here
        return null;
    }

    @Override
    public Integer executeUpdate(SQLUpdateQuery query) throws SQLException {
        //TODO: Calls Come Here
        return null;
    }

    @Override
    public Integer executeDelete(SQLDeleteQuery query) throws SQLException {
        //TODO: Calls Come Here
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
