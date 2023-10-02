package com.infoworks.lab.components.crud.components.datasource;

import com.it.soul.lab.sql.entity.EntityInterface;
import com.vaadin.flow.data.provider.DataProvider;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;

public abstract class AbstractGridDataSource<E extends EntityInterface> implements GridDataSource<E> {

    protected Logger LOG = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void reloadGrid() {
        //Reload Provider
        getGrid().getDataProvider().refreshAll();
        //Updating UI
        if (Objects.isNull(getProvider())) {
            //Setting storage's values as a container to the ListDataProvider,
            //which will take care of adding and removing from Grid in respect to,
            //item removing from storage:
            setProvider(DataProvider.ofCollection(getMemStorage().values()));
            getGrid().setDataProvider(getProvider());
        }
    }

    @Override
    public GridDataSource save(E item) {
        if (!getMemStorage().containsKey(item.hashCode())){
            //Add New:
            addItems(item);
        }else{
            //Update:
            updateItems(item);
        }
        return this;
    }

    protected synchronized void addItems(E...items){
        Arrays.stream(items).forEach(e -> getMemStorage().put(e.hashCode(), e));
    }

    protected synchronized void updateItems(E...items){
        Arrays.stream(items).forEach(item -> {
            E element = getMemStorage().get(item.hashCode());
            if (Objects.nonNull(element)) {
                Map updated = item.marshallingToMap(false);
                element.unmarshallingFromMap(updated, false);
            }
        });
    }

    @Override
    public GridDataSource delete(E item) {
        E element = getMemStorage().get(item.hashCode());
        if (Objects.nonNull(element)){
            //Remove
            removeItems(element);
        }
        return this;
    }

    protected synchronized void removeItems(E...items){
        Arrays.stream(items).forEach(e -> getMemStorage().remove(e.hashCode()));
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
    public GridDataSource registerMultiSelectCallback(Consumer<Set<E>> consumer) {
        getGrid().asMultiSelect().addValueChangeListener(event -> {
            Set<E> selectedElements = (Set<E>) event.getValue();
            if (consumer != null)
                consumer.accept(selectedElements);

        });
        return this;
    }

}
