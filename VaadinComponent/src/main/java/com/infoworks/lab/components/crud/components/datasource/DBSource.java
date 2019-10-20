package com.infoworks.lab.components.crud.components.datasource;

import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.QueryType;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.SQLScalarQuery;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DBSource<E extends Entity> extends CachedSource<E> implements JsqlDataSource<E> {

    @Override
    public void reloadGrid() {
        //Reload Provider
        getGrid().getDataProvider().refreshAll();
        //Updating UI
        if (Objects.nonNull(getGrid())
                && Objects.isNull(getProvider())) {
            DataProvider<E, Void> provider = DataProvider.fromCallbacks(query -> {
                SQLSelectQuery select = getSelectQuery(query);
                try {
                    List<E> items = getExecutor().executeSelect(select
                            , getBeanType()
                            , Entity.mapColumnsToProperties(getBeanType()));
                    return items.stream();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                //return empty List:
                return new ArrayList<E>().stream();
            }, query -> {
                SQLScalarQuery scalarQuery = getCountQuery(query);
                try {
                    return getExecutor().getScalarValue(scalarQuery);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return 0;
            });
            //
            setProvider(provider);
            getGrid().setDataProvider(getProvider());
        }
    }

    @Override
    public GridDataSource addSearchFilter(String filter) {
        //TODO:
        return this;
    }

    private QueryExecutor executor;
    @Override
    public QueryExecutor getExecutor() {
        return executor;
    }

    @Override
    public JsqlDataSource setExecutor(QueryExecutor executor) {
        this.executor = executor;
        return this;
    }

    private DataProvider<E, Void> provider;

    @Override
    public DataProvider getProvider() {
        return provider;
    }

    @Override
    public GridDataSource setProvider(DataProvider provider) {
        this.provider = provider;
        return this;
    }

    @Override
    public SQLSelectQuery getSelectQuery(Query query) {
        SQLSelectQuery selectQuery = new SQLQuery.Builder(QueryType.SELECT)
                .columns()
                .from(E.tableName(getBeanType()))
                .addLimit(query.getOffset(), query.getLimit()).build();
        return selectQuery;
    }

    @Override
    public SQLScalarQuery getCountQuery(Query query) {
        SQLScalarQuery scalarQuery = new SQLQuery.Builder(QueryType.COUNT)
                .columns()
                .on(E.tableName(getBeanType()))
                .build();
        return scalarQuery;
    }

    @Override
    public GridDataSource save(E item) {

        if (Objects.isNull(item)) return this;

        Object id = getProvider().getId(item);
        if (Objects.nonNull(id)){
            try {
                item.update(getExecutor());
            } catch (SQLException e) {
                LOG.warning(e.getMessage());
            }
        }else {
            try {
                item.insert(getExecutor());
            } catch (SQLException e) {
                LOG.warning(e.getMessage());
            }
        }
        return this;
    }

    @Override
    public GridDataSource delete(E item) {

        if (Objects.isNull(item)) return this;

        Object id = getProvider().getId(item);
        if (Objects.nonNull(id)){
            try {
                item.delete(getExecutor());
            } catch (SQLException e) {
                LOG.warning(e.getMessage());
            }
        }
        return this;
    }

}
