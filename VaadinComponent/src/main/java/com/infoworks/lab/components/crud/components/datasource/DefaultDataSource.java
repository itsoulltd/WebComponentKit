package com.infoworks.lab.components.crud.components.datasource;

import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.models.Property;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;

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
        }
        return this;
    }

    @Override
    public GridDataSource prepareGridUI(Grid<E> grid) {
        super.reloadGrid();
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

    @Override
    public GridDataSource addSearchFilter(String filter) {
        if (Objects.nonNull(getGrid())
                && Objects.nonNull(getProvider())){
            //
            if (getProvider() instanceof ListDataProvider){
                ListDataProvider<E> dProvider = (ListDataProvider<E>) getProvider();
                //
                dProvider.clearFilters();
                dProvider.addFilter(item -> {
                    Map data = item.marshallingToMap(false);
                    return data.values().stream()
                            .filter(o -> Objects.nonNull(o))
                            .anyMatch(o -> o.toString().toLowerCase().contains(filter.toLowerCase()));
                });
            }
        }
        return this;
    }

    @Override
    public GridDataSource addSearchFilters(int limit, int offset, Property... filters) {
        if (filters.length == 0) return this;
        if (Objects.isNull(filters[0].getValue())) throw new RuntimeException("Filter Value Must Not Be Null!");
        return addSearchFilter(filters[0].getValue().toString());
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
