package com.infoworks.lab.components.rest.source;

import com.infoworks.lab.components.crud.components.datasource.GridDataSource;
import com.infoworks.lab.components.db.source.SqlDataSource;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.vaadin.flow.data.provider.Query;

public class RestDataSource<E extends Entity> extends SqlDataSource<E> {

    @Override
    public GridDataSource addSearchFilter(String filter) {
        return super.addSearchFilter(filter);
    }

    @Override
    public SQLSelectQuery getSearchQuery(Query<E, String> query) {
        return super.getSearchQuery(query);
    }

    @Override
    public SQLSelectQuery getSelectQuery(Query<E, String> query) {
        return super.getSelectQuery(query);
    }
}
