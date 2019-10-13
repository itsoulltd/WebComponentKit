package com.infoworks.lab.components.crud.components.editor;

import com.it.soul.lab.sql.entity.EntityInterface;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EmbeddedBeanEditor<T extends EntityInterface> extends AbstractBeanEditor<T> {

    private Class<T> beanType;

    public EmbeddedBeanEditor(Class<T> beanType) {
        this(beanType, (item, opt)->{}, (item)->{});
    }

    public EmbeddedBeanEditor(Class<T> beanType, BiConsumer<T, Operation> itemSaver, Consumer<T> itemDeleter) {
        this(beanType.getSimpleName(), beanType, itemSaver, itemDeleter);
    }

    public EmbeddedBeanEditor(String itemType, Class<T> beanType, BiConsumer<T, Operation> itemSaver, Consumer<T> itemDeleter) {
        super(itemType, itemSaver, itemDeleter);
        this.beanType = beanType;
    }

    private Binder<T> binder;

    @Override
    protected Binder<T> getBinder() {
        if (binder == null) binder = new BeanValidationBinder(beanType);
        return binder;
    }

    @Override
    protected void confirmDelete() {
        openConfirmationDialog("Delete Item!","Are You Sure?","");
    }

    @Override
    public String[] propertyKeys(T item) {
        return super.propertyKeys(item);
    }

}
