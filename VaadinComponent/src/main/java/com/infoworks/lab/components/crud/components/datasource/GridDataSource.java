package com.infoworks.lab.components.crud.components.datasource;

import com.it.soul.lab.sql.query.models.Property;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface GridDataSource<T> {
    Map<Object, T> getMemStorage();
    Class<T> getBeanType();
    GridDataSource setBeanType(Class<T> beanType);
    GridDataSource setDefaultColumns(String...columns);
    String[] getDefaultColumns();
    void reloadGrid();
    GridDataSource setGrid(Grid<T> grid);
    Grid getGrid();
    GridDataSource prepareGridUI(Grid<T> grid);
    DataProvider getProvider();
    GridDataSource setProvider(DataProvider provider);
    GridDataSource addSearchFilter(String filter);
    GridDataSource addSearchFilters(int limit, int offset, Property...filters);
    GridDataSource save(T item);
    GridDataSource delete(T item);
    GridDataSource registerSingleSelectCallback(Consumer<T> consumer);
    GridDataSource registerMultiSelectCallback(Consumer<Set<T>> consumer);
}
