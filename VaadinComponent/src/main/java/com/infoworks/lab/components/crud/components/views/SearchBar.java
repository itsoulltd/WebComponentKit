package com.infoworks.lab.components.crud.components.views;

import com.it.soul.lab.sql.entity.EntityInterface;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class SearchBar<T extends EntityInterface> extends Composite<Div> {

    private Class<T> beanType;
    private TextField searchField;
    private Button newButton;
    private SearchBarConfigurator configurator;

    public SearchBar(Class<T> beanType, SearchBarConfigurator configurator){
        this(beanType, configurator, (event)->{}, (event)->{});
    }

    public SearchBar(Class<T> beanType, SearchBarConfigurator configurator, HasValue.ValueChangeListener changeListener, ComponentEventListener clickEvent) {
        this.beanType = beanType;
        this.configurator = configurator;

        HorizontalLayout layout = new HorizontalLayout();
        //layout.setSpacing(true);
        //layout.setPadding(false);
        //layout.setWidth("100%");

        searchField = new TextField();
        searchField.setLabel("");
        searchField.setPlaceholder("Search By Any...");
        searchField.setPrefixComponent(new Icon("lumo", "search"));
        searchField.addValueChangeListener(changeListener);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        layout.add(searchField);

        newButton = new Button("Add New", new Icon("lumo", "plus"));
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(clickEvent);
        newButton.addClickShortcut(Key.of("+"));

        if (!configurator.isHideAddNewButton())
            layout.add(newButton);

        //getContent().setSizeUndefined();
        getContent().add(layout);
    }

    public void addValueChangeListener(HasValue.ValueChangeListener listener){
        searchField.addValueChangeListener(listener);
    }

    public void addClickListener(ComponentEventListener listener){
        newButton.addClickListener(listener);
    }

    public static class SearchBarConfigurator{
        private boolean hideAddNewButton;

        public boolean isHideAddNewButton() {
            return hideAddNewButton;
        }

        public SearchBarConfigurator setHideAddNewButton(boolean hideAddNewButton) {
            this.hideAddNewButton = hideAddNewButton;
            return this;
        }
    }

}
