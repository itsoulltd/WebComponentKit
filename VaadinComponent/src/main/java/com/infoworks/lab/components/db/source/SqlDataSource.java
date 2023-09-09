package com.infoworks.lab.components.db.source;

import com.infoworks.lab.components.crud.components.datasource.GridDataSource;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.SQLScalarQuery;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.models.Predicate;
import com.it.soul.lab.sql.query.models.Property;
import com.it.soul.lab.sql.query.models.Where;
import com.vaadin.flow.data.provider.Query;

import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Stream;

public class SqlDataSource<E extends Entity> extends AbstractJsqlDataSource<E> {

    @Override
    public GridDataSource addSearchFilter(String filter) {
        if (filter.length() <= 3) {
            if (filter.length() <= 0){
                SQLSelectQuery query = getSearchQuery(copyWith(getQuery(), null));
                executeQuery(query);
                return super.addSearchFilter("");
            }else {
                return super.addSearchFilter(filter);
            }
        }
        Query query = copyWith(getQuery(), filter);
        SQLSelectQuery sqlquery = getSearchQuery(query);
        executeQuery(sqlquery);
        reloadGrid();
        return this;
    }

    @Override
    public GridDataSource addSearchFilters(int limit, int offset, Property... filters) {
        if (filters.length == 0) return this;
        //TODO:
        Predicate clause = null;
        if (filters.length > 1) {
            for (Property searchProperty : filters) {
                if (Objects.isNull(searchProperty.getValue()))
                    continue;
                if (clause == null)
                    clause = new Where(searchProperty.getKey()).isEqualTo(searchProperty.getValue().toString());
                else
                    clause.or(searchProperty.getKey()).isEqualTo(searchProperty.getValue().toString());
            }
        } else {
            Property searchProperty = filters[0];
            if (Objects.isNull(searchProperty.getValue())) throw new RuntimeException("Filter Value Must Not Be Null!");
            clause = new Where(searchProperty.getKey()).isLike("%" + searchProperty.getValue().toString() + "%");
        }
        if (clause != null){
            SQLSelectQuery selectQuery = new SQLQuery.Builder(QueryType.SELECT)
                    .columns()
                    .from(E.tableName(getBeanType()))
                    .where(clause)
                    .addLimit(limit, offset)
                    .build();
            //Finally Execute the search:
            executeQuery(selectQuery);
            reloadGrid();
        }
        return this;
    }

    @Override
    public SQLSelectQuery getSearchQuery(Query<E, String> query) {
        SQLSelectQuery selectQuery = null;
        Predicate clause = createSearchPredicate(query);
        if (clause != null){
            selectQuery = new SQLQuery.Builder(QueryType.SELECT)
                    .columns()
                    .from(E.tableName(getBeanType()))
                    .where(clause)
                    //.addLimit(query.getLimit(), query.getOffset())
                    .build();
        }
        return selectQuery;
    }

    @Override
    public SQLSelectQuery getSelectQuery(Query<E, String> query) {
        SQLSelectQuery selectQuery = new SQLQuery.Builder(QueryType.SELECT)
                    .columns()
                    .from(E.tableName(getBeanType()))
                    .addLimit(query.getLimit(), query.getOffset())
                    .build();
        return selectQuery;
    }

    public int getRowCount(){
        try {
            int max = getExecutor().getScalarValue(getCountQuery());
            return max;
        } catch (SQLException e) {
            LOG.warning(e.getMessage());
        }
        return getQuery().getOffset();
    }

    protected SQLScalarQuery getCountQuery() {
        SQLScalarQuery scalarQuery = new SQLQuery.Builder(QueryType.COUNT)
                .columns()
                .on(E.tableName(getBeanType()))
                .build();
        return scalarQuery;
    }

}
