package com.infoworks.lab.components.db.source;

import com.it.soul.lab.sql.SQLExecutor;
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

    private Query maxLimitQuery;

    public Query getMaxOffsetQuery() {
        if (maxLimitQuery == null){
            if (getExecutor() instanceof SQLExecutor){
                try {
                    int max = ((SQLExecutor)getExecutor()).getScalarValue(getCountQuery());
                    maxLimitQuery = new Query(max
                            , getQuery().getLimit()
                            , getQuery().getSortOrders()
                            , getQuery().getInMemorySorting()
                            , getQuery().getFilter().isPresent() ? getQuery().getFilter().get() : null);
                } catch (SQLException e) {
                    e.printStackTrace();
                    maxLimitQuery = getQuery();
                }
            }
        }
        return maxLimitQuery;
    }

    @Override
    public Query<E, String> updateMaxOffsetQuery(int byValue) {
        Query max = new Query(getMaxOffsetQuery().getOffset() + (byValue)
                            , getMaxOffsetQuery().getLimit()
                            , getMaxOffsetQuery().getSortOrders()
                            , getMaxOffsetQuery().getInMemorySorting()
                            , getMaxOffsetQuery().getFilter().isPresent() ? getMaxOffsetQuery().getFilter().get() : null);
        this.maxLimitQuery = max;
        updateCellFooter(getGrid());
        return max;
    }

    protected SQLScalarQuery getCountQuery() {
        SQLScalarQuery scalarQuery = new SQLQuery.Builder(QueryType.COUNT)
                .columns()
                .on(E.tableName(getBeanType()))
                .build();
        return scalarQuery;
    }

}
