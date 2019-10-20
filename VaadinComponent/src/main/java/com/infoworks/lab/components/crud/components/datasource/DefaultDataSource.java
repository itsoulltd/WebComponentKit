package com.infoworks.lab.components.crud.components.datasource;

import com.it.soul.lab.sql.entity.Entity;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class DefaultDataSource<E extends Entity> extends AbstractGridDataSource<E> {

    private Map<Object, E> storage = new ConcurrentHashMap<>();

    @Override
    public Map<Object, E> getMemStorage() {
        return storage;
    }

    private Grid grid;

    @Override
    public GridDataSource setGrid(Grid grid) {
        if (Objects.nonNull(grid))
            this.grid = grid;
        reloadGrid();
        return this;
    }

    @Override
    public Grid getGrid() {
        return grid;
    }

    private DataProvider provider;

    @Override
    public DataProvider getProvider() {
        return provider;
    }

    @Override
    public GridDataSource setProvider(DataProvider provider) {
        this.provider = provider;
        return this;
    }

    private Class<E> beanType;

    @Override
    public Class<E> getBeanType() {
        return beanType;
    }

    @Override
    public GridDataSource setBeanType(Class<E> beanType) {
        this.beanType = beanType;
        return this;
    }
}
