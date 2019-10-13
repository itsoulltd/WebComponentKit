package com.infoworks.lab.components.crud.components.editor;

import com.it.soul.lab.sql.entity.EntityInterface;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface BeanEditor<T extends EntityInterface> {
    void prepare(T item, AbstractBeanEditor.Operation operation);
    String[] propertyKeys(T item);
    void clear();
    void setClearButtonTitle(String text);
    void addSaveClickListener(BiConsumer<T, AbstractBeanEditor.Operation> itemSaver);
    void addDeleteClickListener(Consumer<T> itemDeleter);
    void addClearClickListener(ComponentEventListener<ClickEvent<Button>> listener);
}
