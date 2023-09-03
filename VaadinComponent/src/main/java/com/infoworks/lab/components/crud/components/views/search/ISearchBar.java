package com.infoworks.lab.components.crud.components.views.search;

import com.infoworks.lab.components.crud.Configurator;
import com.infoworks.lab.components.crud.components.editor.BeanDialog;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;

public interface ISearchBar<T extends EntityInterface> {
    void addValueChangeListener(HasValue.ValueChangeListener listener);
    void addClickListener(ComponentEventListener listener);
    void configureDefaultEvents(Configurator configurator, BeanDialog dialog);
    Component prepareSearchView(SearchBarConfigurator configurator);
}
