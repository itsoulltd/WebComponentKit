package com.infoworks.lab.components.crud.components.views.search;

import com.infoworks.lab.components.crud.Configurator;
import com.infoworks.lab.components.crud.components.editor.AbstractBeanEditor;
import com.infoworks.lab.components.crud.components.editor.BeanDialog;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class SearchBar<T extends EntityInterface> extends Composite<Div> implements ISearchBar<T> {

    private Class<T> beanType;
    private SearchBarConfigurator configurator;
    private TextField searchField;
    private Button newButton;

    public SearchBar(Class<T> beanType, SearchBarConfigurator configurator){
        this.beanType = beanType;
        this.configurator = configurator;
        //
        //getContent().setSizeUndefined();
        Component barLayout = prepareSearchView(configurator);
        getContent().add(barLayout);
    }

    @Override
    public Component prepareSearchView(SearchBarConfigurator configurator) {
        HorizontalLayout layout = new HorizontalLayout();

        searchField = new TextField();
        searchField.setLabel("");
        searchField.setPlaceholder("Search By Any...");
        searchField.setPrefixComponent(new Icon("lumo", "search"));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        layout.add(searchField);

        if (!configurator.isHideAddNewButton()) {
            newButton = new Button("Add New", new Icon("lumo", "plus"));
            newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            newButton.addClickShortcut(Key.NEW);
            layout.add(newButton);
        }

        return layout;
    }

    public void addValueChangeListener(HasValue.ValueChangeListener listener) {
        searchField.addValueChangeListener(listener);
    }

    public void addClickListener(ComponentEventListener listener){
        if (!getConfigurator().isHideAddNewButton()) {
            newButton.addClickListener(listener);
        }
    }

    @Override
    public void configureDefaultEvents(Configurator configurator, BeanDialog dialog) {
        //Action on AddNew Button on SearchBar:
        addClickListener((event) -> {
            try {
                EntityInterface ei = configurator.getBeanType().newInstance();
                dialog.open((T) ei, AbstractBeanEditor.Operation.ADD);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        //Action on value-changed event on Search Field:
        addValueChangeListener((event) ->
                configurator.getDataSource().addSearchFilter(event.getValue().toString())
        );
    }

    public Class<T> getBeanType() {
        return beanType;
    }

    public SearchBarConfigurator getConfigurator() {
        return configurator;
    }

    @Override
    public void clearSearchBarView() {
        //TODO:
    }
}
