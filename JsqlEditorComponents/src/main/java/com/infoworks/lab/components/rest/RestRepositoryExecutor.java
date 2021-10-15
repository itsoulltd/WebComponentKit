package com.infoworks.lab.components.rest;

import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.repository.RestRepository;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.*;
import com.it.soul.lab.sql.query.builder.AbstractQueryBuilder;
import com.it.soul.lab.sql.query.models.Row;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class RestRepositoryExecutor extends AbstractRestExecutor{

    private final RestRepository repository;
    private int maxCount;
    private static Logger LOG = Logger.getLogger(RestRepositoryExecutor.class.getSimpleName());

    public RestRepositoryExecutor(RestRepository repository) {
        this.repository = repository;
    }

    protected RestRepository getRepository() {
        return repository;
    }

    public int getMaxCount() {
        return maxCount;
    }

    @Override
    public AbstractQueryBuilder createQueryBuilder(QueryType queryType) {
        return new SQLQuery.Builder(queryType);
    }

    @Override
    public Integer executeInsert(boolean b, SQLInsertQuery sqlInsertQuery) throws SQLException, IllegalArgumentException {
        try {
            Entity request = (Entity) sqlInsertQuery.getRow().inflate(getRepository().getEntityType());
            Entity created = getRepository().insert(request);
            return created != null ? 1 : 0;
        } catch (RuntimeException e) {
            throw new SQLException(e.fillInStackTrace());
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e.fillInStackTrace());
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e.fillInStackTrace());
        }
    }

    @Override
    public Integer executeUpdate(SQLUpdateQuery sqlUpdateQuery) throws SQLException {
        try {
            Entity request = (Entity) sqlUpdateQuery.getRow().inflate(getRepository().getEntityType());
            Row row = sqlUpdateQuery.getWhereProperties();
            Map kvo = row.keyObjectMap();
            Object id = kvo.get(getRepository().getPrimaryKeyName());
            Entity updated = getRepository().update(request, id);
            return updated != null ? 1 : 0;
        } catch (RuntimeException e) {
            throw new SQLException(e.fillInStackTrace());
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e.fillInStackTrace());
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e.fillInStackTrace());
        }
    }

    @Override
    public Integer executeDelete(SQLDeleteQuery sqlDeleteQuery) throws SQLException {
        try {
            Row row = sqlDeleteQuery.getWhereProperties();
            Map kvo = row.keyObjectMap();
            Object id = kvo.get(getRepository().getPrimaryKeyName());
            boolean isDeleted = getRepository().delete(id);
            LOG.info("Is Deleted: " + isDeleted);
            return isDeleted ? 1 : 0;
        } catch (RuntimeException e) {
            throw new SQLException(e.fillInStackTrace());
        }
    }

    @Override
    public Integer getScalarValue(SQLScalarQuery sqlScalarQuery) throws SQLException {
        try {
            ItemCount count = getRepository().rowCount();
            maxCount = count.getCount().intValue();
        } catch (RuntimeException e) {
            throw new SQLException(e.fillInStackTrace());
        }
        return maxCount;
    }

    @Override
    public <T> List<T> executeSelect(SQLSelectQuery sqlSelectQuery, Class<T> aClass, Map<String, String> map) throws SQLException, IllegalArgumentException, IllegalAccessException, InstantiationException {
        try {
            int limit = sqlSelectQuery.getLimit();
            int offset = sqlSelectQuery.getOffset();
            int page = offset / limit;
            if (offset > maxCount) return new ArrayList();
            LOG.info(String.format("Offset:%s, Limit:%s, Page:%s", offset, limit, page));
            List returned = getRepository().fetch(page, limit);
            return returned;
        } catch (RuntimeException e) {
            throw new SQLException(e.fillInStackTrace());
        }
    }
}
