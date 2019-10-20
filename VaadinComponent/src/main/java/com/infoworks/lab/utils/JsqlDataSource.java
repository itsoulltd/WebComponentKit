package com.infoworks.lab.utils;

import com.infoworks.lab.components.crud.components.datasource.GridDataSource;
import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.it.soul.lab.sql.query.SQLScalarQuery;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.vaadin.flow.data.provider.Query;

public interface JsqlDataSource<T extends EntityInterface> extends GridDataSource<T> {
    QueryExecutor getExecutor();
    JsqlDataSource setExecutor(QueryExecutor executor);
    SQLSelectQuery getSelectQuery(Query query);
    SQLScalarQuery getCountQuery(Query query);
}
