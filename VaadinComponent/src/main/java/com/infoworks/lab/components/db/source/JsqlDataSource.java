package com.infoworks.lab.components.db.source;

import com.infoworks.lab.components.crud.components.datasource.GridDataSource;
import com.infoworks.lab.jsql.DataSourceKey;
import com.infoworks.lab.jsql.ExecutorType;
import com.infoworks.lab.jsql.JsqlConfig;
import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.vaadin.flow.data.provider.Query;

public interface JsqlDataSource<T extends EntityInterface> extends GridDataSource<T> {

    QueryExecutor getExecutor();
    JsqlDataSource setExecutor(QueryExecutor executor);
    SQLSelectQuery getSelectQuery(Query<T, String> query);
    SQLSelectQuery getSearchQuery(Query<T, String> query);
    int getRowCount();

    static <GDS extends GridDataSource> GDS createDataSource(Class<GDS> type, ExecutorType executorType, DataSourceKey container)
            throws RuntimeException {
        JsqlConfig config = new JsqlConfig();
        GDS source = null;
        try {
            source = type.newInstance();
            if (JsqlDataSource.class.isAssignableFrom(type)){
                QueryExecutor executor = config.create(executorType, container.get(DataSourceKey.Keys.NAME), container);
                ((JsqlDataSource<EntityInterface>)source).setExecutor(executor);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
        return source;
    }

    static <GDS extends GridDataSource> GDS createDataSource(Class<GDS> type, ExecutorType executorType){
        return createDataSource(type, executorType, JsqlConfig.createDataSourceKey("app.db"));
    }
}
