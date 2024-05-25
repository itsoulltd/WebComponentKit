package com.infoworks.lab.components.crud.components.views.search;

import com.infoworks.lab.components.crud.Configurator;
import com.infoworks.lab.components.crud.components.editor.AbstractBeanEditor;
import com.infoworks.lab.components.crud.components.editor.BeanDialog;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.it.soul.lab.sql.query.models.Property;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.List;

public class SearchBar<T extends EntityInterface> extends Composite<Div> implements ISearchBar<T> {

    public static final String CLEAR_BUTTON_TITLE = "Clear";
    public static final String SEARCH_BUTTON_TITLE = "Search";

    private Class<T> beanType;
    private SearchBarConfigurator configurator;
    private TextField searchField;
    private Button newButton;
    private Button searchButton;

    public SearchBar(Class<T> beanType, SearchBarConfigurator configurator){
        this.beanType = beanType;
        this.configurator = configurator;
        //
        //getContent().setSizeUndefined();
        Component barLayout = prepareSearchView(configurator);
        getContent().add(barLayout);
    }

    @Override @SuppressWarnings("Duplicates")
    public Component prepareSearchView(SearchBarConfigurator configurator) {
        HorizontalLayout layout = new HorizontalLayout();

        searchField = new TextField();
        searchField.setLabel("");
        searchField.setPlaceholder("Search By Any...");
        searchField.setPrefixComponent(new Icon("lumo", "search"));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        layout.add(searchField);

        searchButton = new Button(SEARCH_BUTTON_TITLE, new Icon("lumo", "search"));
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.addClickShortcut(Key.ENTER);
        layout.add(searchButton);

        if (!configurator.isHideAddNewButton()) {
            newButton = new Button("Add New", new Icon("lumo", "plus"));
            newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            newButton.addClickShortcut(Key.NEW);
            layout.add(newButton);
        }

        return layout;
    }

    private void alterButton(Button button, String title, Icon icon) {
        if (button.getText().equalsIgnoreCase(title))
            return;
        button.setText(title);
        button.setIcon(icon);
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
        searchField.addValueChangeListener(((event) -> {
            //Just update the search-button ui:
            alterButton(searchButton, SEARCH_BUTTON_TITLE, new Icon("lumo", "search"));
        }));
        //Action on searchButton:
        final String[] skipProps = getConfigurator().getSkipProperties();
        final List<Property> searchProps = getConfigurator().getProperties(getBeanType(), skipProps);
        searchButton.addClickListener((event) -> {
            if (event.getSource().getText().equalsIgnoreCase(CLEAR_BUTTON_TITLE)) {
                //Clear the grid & related buttons:
                clearSearchBarView();
                configurator.getDataSource().reloadGrid();
            } else {
                //Do not update grid when result comes from rest-api.
                //configurator.getDataSource().addSearchFilter(event.getValue().toString());
                String query = searchField.getValue();
                searchProps.forEach(property -> property.setValue(query));
                configurator.getDataSource().addSearchFilters(configurator.getGridPageSize()
                        , 0
                        , searchProps.toArray(new Property[0]));
                alterButton(event.getSource(), CLEAR_BUTTON_TITLE, new Icon("lumo", "cross"));
            }
        });
    }

    public Class<T> getBeanType() {
        return beanType;
    }

    public SearchBarConfigurator getConfigurator() {
        return configurator;
    }

    @Override
    public void clearSearchBarView() {
        //Since searchField clear will trigger the valueChange listener, eventually button text will change.
        searchField.clear();
    }
}
