package com.infoworks.lab.components.crud.components.editor;

import com.it.soul.lab.sql.entity.EntityInterface;

public interface BeanDialog<T extends EntityInterface> extends BeanEditor<T> {
    void open(T item, AbstractBeanEditor.Operation operation);
    void close();
}
