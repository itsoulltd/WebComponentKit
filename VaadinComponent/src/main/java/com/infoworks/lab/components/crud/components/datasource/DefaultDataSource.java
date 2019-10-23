package com.infoworks.lab.components.crud.components.datasource;

import com.it.soul.lab.sql.entity.Entity;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDataSource<E extends Entity> extends AbstractGridDataSource<E> {

    private Map<Object, E> storage = new ConcurrentHashMap<>();

    @Override
    public Map<Object, E> getMemStorage() {
        return storage;
    }

    private Grid grid;

    @Override
    public GridDataSource setGrid(Grid<E> grid) {
        if (Objects.nonNull(grid)) {
            this.grid = grid;
            //Setting Default:
            this.grid.setPageSize(10);
        }
        return this;
    }

    @Override
    public GridDataSource prepareGridUI(Grid<E> grid) {
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

    private List<String> columns;

    @Override
    public GridDataSource setDefaultColumns(String... columns) {
        this.columns = Arrays.asList(columns);
        if (Objects.nonNull(getGrid())){
            getGrid().setColumns(columns);
        }
        return this;
    }

    @Override
    public String[] getDefaultColumns() {
        return this.columns.toArray(new String[0]);
    }
}
