package com.infoworks.lab.components.db.source;

import com.it.soul.lab.jpql.query.JPQLQuery;
import com.it.soul.lab.jpql.service.JPQLExecutor;
import com.it.soul.lab.sql.SQLExecutor;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.models.Predicate;
import com.vaadin.flow.data.provider.Query;

public class JpqlDataSource <E extends Entity> extends AbstractJsqlDataSource<E>{

    @Override
    public SQLSelectQuery getSearchQuery(Query<E, String> query) {
        Predicate clause = createSearchPredicate(query);
        SQLSelectQuery selectQuery = null;
        if (clause != null){
            selectQuery = new JPQLQuery.Builder(QueryType.SELECT)
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
        SQLSelectQuery selectQuery = new JPQLQuery.Builder(QueryType.SELECT)
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
                    int max = ((JPQLExecutor)getExecutor()).rowCount(getBeanType());
                    maxLimitQuery = new Query(max
                            , getQuery().getLimit()
                            , getQuery().getSortOrders()
                            , getQuery().getInMemorySorting()
                            , getQuery().getFilter().isPresent() ? getQuery().getFilter().get() : null);
                } catch (Exception e) {
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

}
