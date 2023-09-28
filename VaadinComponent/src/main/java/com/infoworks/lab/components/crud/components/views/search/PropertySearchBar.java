package com.infoworks.lab.components.crud.components.views.search;

import com.infoworks.lab.components.crud.Configurator;
import com.infoworks.lab.components.crud.components.editor.AbstractBeanEditor;
import com.infoworks.lab.components.crud.components.editor.BeanDialog;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.it.soul.lab.sql.query.models.Property;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class PropertySearchBar<T extends EntityInterface> extends SearchBar<T> {

    public static final String CLEAR_BUTTON_TITLE = "Clear";
    public static final String SEARCH_BUTTON_TITLE = "Search";
    private TextField searchField;
    private Button newButton;
    private Property searchProperty;
    private Button searchButton;
    private ComboBox<Property> propertyComboBox;

    public PropertySearchBar(Class<T> beanType, SearchBarConfigurator configurator) {
        super(beanType, configurator);
    }

    @Override
    public Component prepareSearchView(SearchBarConfigurator configurator) {
        HorizontalLayout layout = new HorizontalLayout();

        propertyComboBox = new ComboBox<>();
        String[] skipProps = configurator.getSkipProperties();
        propertyComboBox.setItems(configurator.getProperties(getBeanType(), skipProps));
        propertyComboBox.setItemLabelGenerator(Property::getKey);
        layout.add(propertyComboBox);

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

    @Override
    public void configureDefaultEvents(Configurator configurator, BeanDialog dialog) {
        //Action on AddNew Button on SearchBar:
        if (!getConfigurator().isHideAddNewButton()) {
            newButton.addClickListener(((event) -> {
                try {
                    EntityInterface ei = configurator.getBeanType().newInstance();
                    dialog.open((T) ei, AbstractBeanEditor.Operation.ADD);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }));
        }
        //Action setting up change event:
        propertyComboBox.addValueChangeListener((event) -> {
            //System.out.println(event.getValue().getKey());
            searchProperty = event.getValue();
            alterButton(searchButton, SEARCH_BUTTON_TITLE, new Icon("lumo", "search"));
        });
        //Action on value-changed event on Search Field:
        searchField.addValueChangeListener(((event) -> {
            if (searchProperty != null) {
                System.out.println("Search With: " + event.getValue());
                searchProperty.setValue(event.getValue());
            }
            alterButton(searchButton, SEARCH_BUTTON_TITLE, new Icon("lumo", "search"));
        }));
        //Action on searchButton:
        searchButton.addClickListener((event) -> {
            if (event.getSource().getText().equalsIgnoreCase(CLEAR_BUTTON_TITLE)) {
                //Clear the grid & related buttons:
                searchField.clear();
                propertyComboBox.clear();
                configurator.getDataSource().reloadGrid();
            } else {
                //Does not update grid when result comes from rest-api.
                //configurator.getDataSource().addSearchFilter(event.getValue().toString());
                if (searchProperty != null) {
                    if (searchProperty.getValue() == null) {
                        searchProperty.setValue(searchField.getValue());
                    }
                    configurator.getDataSource().addSearchFilters(configurator.getGridPageSize()
                            , 0
                            , searchProperty);
                    alterButton(event.getSource(), CLEAR_BUTTON_TITLE, new Icon("lumo", "cross"));
                }
            }
        });
    }

}
