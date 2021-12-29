package com.infoworks.lab.rest.repository;

import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.builder.AbstractQueryBuilder;
import com.it.soul.lab.sql.query.models.Where;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface iGenericRepository<E extends Entity, ID> extends iRepository<E, ID> {

    QueryExecutor getExecutor();

    default int fromIdx(SearchQuery searchQuery) {
        int idx = (searchQuery.getPage() - 1) * searchQuery.getSize();
        return (idx < 0) ? 0 : idx;
    }

    default int toIdx(SearchQuery searchQuery, int fromIdx, int itemsSize) {
        int toIdx = fromIdx + searchQuery.getSize();
        return (itemsSize < toIdx) ? itemsSize : toIdx;
    }

    /**
     * @param searchQuery
     * @return
     */
    default List<E> search(SearchQuery searchQuery) {
        try {
            SQLQuery query;
            AbstractQueryBuilder queryBuilder = getExecutor().createQueryBuilder(QueryType.SELECT);
            if (searchQuery.getProperties().isEmpty()){
                query = queryBuilder.columns().from(getEntityType()).build();
            }else {
                query = queryBuilder.columns().from(getEntityType()).where(searchQuery.getPredicate()).build();
            }
            List<E> items = getExecutor().executeSelect(query, getEntityType(), Entity.mapColumnsToProperties(getEntityType()));
            int fromIdx = fromIdx(searchQuery);
            int toIdx = toIdx(searchQuery, fromIdx, items.size());
            List<E> res = items.subList(fromIdx, toIdx);
            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param offset
     * @param limit
     * @return
     * @throws RuntimeException
     */
    @Override
    default List<E> fetch(Integer offset, Integer limit) throws RuntimeException {
        try {
            SQLQuery query = getExecutor().createQueryBuilder(QueryType.SELECT)
                    .columns()
                    .from(getEntityType())
                    .build();
            List<E> items = getExecutor().executeSelect(query, getEntityType(), Entity.mapColumnsToProperties(getEntityType()));
            int fromIdx = (offset < 0) ? 0 : offset;
            int toIdx = fromIdx + limit;
            if (items.size() < toIdx) toIdx = items.size();
            List<E> res = items.subList(fromIdx, toIdx);
            return res;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @return
     * @throws RuntimeException
     */
    @Override
    default ItemCount rowCount() throws RuntimeException {
        try {
            SQLQuery query = getExecutor().createQueryBuilder(QueryType.COUNT).columns().on(getEntityType()).build();
            int count = getExecutor().getScalarValue(query);
            ItemCount ic = new ItemCount();
            ic.setCount(Integer.valueOf(count).longValue());
            return ic;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param key: PrimaryKey
     * @return
     */
    default E read(ID key){
        List<E> res = null;
        try {
            res = Entity.read(getEntityType(), getExecutor(), new Where(getPrimaryKeyName()).isEqualTo(key));
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return res != null && res.size() > 0 ? res.get(0) : null;
    }

    /**
     * @param entity
     * @return
     * @throws RuntimeException
     */
    @Override
    default E insert(E entity) throws RuntimeException {
        if (entity == null) return null;
        try {
            entity.insert(getExecutor());
        } catch (SQLException err) {
            throw new RuntimeException(err.getMessage());
        }
        return null;
    }

    /**
     * @param entity
     * @param id
     * @return
     * @throws RuntimeException
     */
    @Override
    default E update(E entity, ID id) throws RuntimeException {
        E existing = read(id);
        if (existing != null && entity != null) {
            Map<String, Object> eData = entity.marshallingToMap(true);
            eData.remove(getPrimaryKeyName());
            existing.unmarshallingFromMap(eData, true);
            try {
                existing.update(getExecutor());
            } catch (SQLException err) {
                throw new RuntimeException(err.getMessage());
            }
        }
        return existing;
    }

    /**
     * @param id
     * @return
     * @throws RuntimeException
     */
    @Override
    default boolean delete(ID id) throws RuntimeException {
        E existing = read(id);
        if (existing != null) {
            try {
                return existing.delete(getExecutor());
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return false;
    }
}
