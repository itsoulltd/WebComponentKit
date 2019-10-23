package com.infoworks.lab.components.db.source;

import com.it.soul.lab.jpql.query.JPQLQuery;
import com.it.soul.lab.jpql.service.JPQLExecutor;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.models.Predicate;
import com.vaadin.flow.data.provider.Query;

public class JpqlDataSource <E extends Entity> extends SqlDataSource<E>{

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

    @Override
    protected int getRowCount() {
        if (getExecutor() instanceof JPQLExecutor){
            try {
                int max = ((JPQLExecutor)getExecutor()).rowCount(getBeanType());
                return max;
            } catch (Exception e) {
                LOG.warning(e.getMessage());
            }
        }
        return super.getRowCount();
    }

}
