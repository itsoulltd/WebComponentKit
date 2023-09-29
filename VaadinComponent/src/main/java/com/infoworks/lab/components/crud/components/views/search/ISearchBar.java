package com.infoworks.lab.components.crud.components.views.search;

import com.infoworks.lab.components.crud.Configurator;
import com.infoworks.lab.components.crud.components.editor.BeanDialog;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.vaadin.flow.component.Component;

public interface ISearchBar<T extends EntityInterface> {
    void configureDefaultEvents(Configurator configurator, BeanDialog dialog);
    Component prepareSearchView(SearchBarConfigurator configurator);
    void clearSearchBarView();
}
