package com.infoworks.lab.components.db.source;

import com.infoworks.lab.components.crud.components.datasource.GridDataSource;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.models.Predicate;
import com.vaadin.flow.component.grid.Grid;
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

    public int getRowCount(){
        try {
            int max = getExecutor().getScalarValue(getCountQuery());
            return max;
        } catch (SQLException e) {
            LOG.warning(e.getMessage());
        }
        return getQuery().getOffset();
    }

    @Override
    public void reloadGrid() {
        super.reloadGrid();
    }

    @Override
    public GridDataSource prepareGridUI(Grid<E> grid) {
        reloadSelectQuery(getQuery());
        return super.prepareGridUI(grid);
    }
}
