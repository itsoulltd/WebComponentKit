package com.infoworks.lab.components.db.source;

import com.infoworks.lab.components.crud.components.datasource.GridDataSource;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.SQLScalarQuery;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.models.Predicate;
import com.vaadin.flow.data.provider.Query;

import java.sql.SQLException;

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
