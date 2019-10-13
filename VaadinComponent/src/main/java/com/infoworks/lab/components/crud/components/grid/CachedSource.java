package com.infoworks.lab.components.crud.components.grid;

import com.it.soul.lab.sql.entity.Entity;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.SerializablePredicate;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class CachedSource<E extends Entity> implements GridDataSource<E> {

    protected Logger LOG = Logger.getLogger(this.getClass().getSimpleName());

    private Grid grid;

    @Override
    public GridDataSource setGrid(Grid grid) {
        if (Objects.nonNull(grid))
            this.grid = grid;
        if (Objects.nonNull(getDefaultColumns()))
            getGrid().setColumns(getDefaultColumns());
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
        return null;
    }

    private String[] defaultColumns;

    public String[] getDefaultColumns() {
        return defaultColumns;
    }

    public GridDataSource setDefaultColumns(String[] defaultColumns) {
        this.defaultColumns = defaultColumns;
        return this;
    }

    private Map<Object, E> storage = new ConcurrentHashMap<>();

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

    @Override
    public void reloadGrid() {
        //Reload Provider
        getGrid().getDataProvider().refreshAll();
        //Updating UI
        if (Objects.nonNull(getGrid())
                && Objects.isNull(getProvider())) {
            //Setting storage's values as a container to the ListDataProvider,
            //which will take care of adding and removing from Grid in respect to,
            //item removing from storage:
            setProvider(DataProvider.ofCollection(storage.values()));
            getGrid().setDataProvider(getProvider());
        }
    }

    @Override
    public GridDataSource save(E item) {
        if (!storage.containsKey(item.hashCode())){
            //Add New:
            addItems(item);
        }else{
            //Update:
            updateItems(item);
        }
        return this;
    }

    private synchronized void addItems(E...items){
        Arrays.stream(items).forEach(e -> storage.put(e.hashCode(), e));
    }

    private synchronized void updateItems(E...items){
        Arrays.stream(items).forEach(item -> {
            E element = storage.get(item.hashCode());
            if (Objects.nonNull(element)) {
                Map updated = item.marshallingToMap(false);
                element.unmarshallingFromMap(updated, false);
            }
        });
    }

    @Override
    public GridDataSource delete(E item) {
        E element = storage.get(item.hashCode());
        if (Objects.nonNull(element)){
            //Remove
            removeItems(element);
        }
        return this;
    }

    private synchronized void removeItems(E...items){
        Arrays.stream(items).forEach(e -> storage.remove(e.hashCode()));
    }

    @Override
    public GridDataSource registerSingleSelectCallback(Consumer<E> consumer) {
        getGrid().asSingleSelect().addValueChangeListener(event -> {
            E el = (E) event.getValue();
            //LOG.info(el.toString());
            if (consumer != null)
                consumer.accept(el);
        });
        return this;
    }

    @Override
    public GridDataSource registerMultiSelecttCallback(Consumer<Set<E>> consumer) {
        getGrid().asMultiSelect().addValueChangeListener(event -> {
            Set<E> selectedElements = (Set<E>) event.getValue();
            if (consumer != null)
                consumer.accept(selectedElements);

        });
        return this;
    }
}
