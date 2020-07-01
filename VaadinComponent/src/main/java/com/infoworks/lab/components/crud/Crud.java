package com.infoworks.lab.components.crud;

import com.infoworks.lab.components.crud.components.editor.*;
import com.infoworks.lab.components.crud.components.views.SearchBar;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class Crud<T extends EntityInterface> extends Composite<Div> {

    private SearchBar searchBar;
    private Configurator configurator;
    private BeanEditor editor;
    private BeanDialog dialog;
    private Component parentLayout;

    public Component getParentLayout() {
        return parentLayout;
    }

    public Crud(Configurator configurator) {
        this.configurator = configurator;
        configurator.getDataSource().setBeanType(configurator.getBeanType());
        Grid grid = new Grid<>(configurator.getBeanType());
        grid.setPageSize(configurator.getGridPageSize());
        configurator.getDataSource().setGrid(grid);
        getGrid().setSelectionMode(configurator.getSelectionMode());
        if (!configurator.isHideSearchBar()) {
            this.searchBar = new SearchBar(configurator.getBeanType(), createSearchBarConfigurator());
        }
        this.parentLayout = prepareParentLayout();
        getContent().add(this.parentLayout);
    }

    protected Component prepareParentLayout(){
        VerticalLayout parent = new VerticalLayout();
        parent.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        if (searchBar != null) {
            parent.add(searchBar);
        }
        parent.add(configurator.getDataSource().getGrid());
        //
        if (configurator.isDialog()) {
            configureDialogForm(parent);
        }
        else if(configurator.isEmbedded()) {
            configureEmbeddedForm(parent);
        }
        else {
            configureDialogForm(parent);
            configureEmbeddedForm(parent);
        }
        prepareGridUI(configurator.getDataSource().getGrid());
        return parent;
    }

    protected void prepareGridUI(Grid grid){
        try {
            EntityInterface ei = configurator.getBeanType().newInstance();
            String[] columns = editor != null ? editor.propertyKeys(ei) : dialog.propertyKeys(ei);
            configurator.getDataSource().setDefaultColumns(columns);
            configurator.getDataSource().prepareGridUI(grid);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void configureEmbeddedForm(VerticalLayout parent){
        try {
            this.editor = configurator.getEditor();
            EntityInterface ei = configurator.getBeanType().newInstance();
            editor.prepare((T) ei, AbstractBeanEditor.Operation.ADD);
            parent.add((Component) editor);
            //
            configurator.getDataSource().registerSingleSelectCallback(item -> {

                editor.prepare((T) item, AbstractBeanEditor.Operation.EDIT);
            });
            //
            this.editor.addSaveClickListener((item, event) -> {

                System.out.println(((T)item).marshallingToMap(false));
                configurator.getDataSource().save((T)item);
                configurator.getDataSource().reloadGrid();
                try {
                    //
                    EntityInterface ein = configurator.getBeanType().newInstance();
                    editor.prepare((T) ein, AbstractBeanEditor.Operation.ADD);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
            //
            this.editor.addDeleteClickListener((item) -> {

                System.out.println(((T)item).marshallingToMap(false));
                configurator.getDataSource().delete((T)item);
                configurator.getDataSource().reloadGrid();
                try {
                    //
                    EntityInterface ein = configurator.getBeanType().newInstance();
                    editor.prepare((T) ein, AbstractBeanEditor.Operation.ADD);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
            //
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void configureDialogForm(VerticalLayout parent){
        this.dialog = configurator.getDialog();
        configureSearchBarEvents();
        //
        this.dialog.addSaveClickListener((item, event) -> {

            System.out.println(((T)item).marshallingToMap(false));
            configurator.getDataSource().save((T)item);
            this.dialog.close();
            configurator.getDataSource().reloadGrid();
        });
    }

    private void configureSearchBarEvents() {
        if (searchBar == null) return;
        //Action on AddNew Button on SearchBar:
        searchBar.addClickListener((event) -> {
            try {
                EntityInterface ei = configurator.getBeanType().newInstance();
                this.dialog.open((T) ei, AbstractBeanEditor.Operation.ADD);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        //Action on value-changed event on Search Field:
        searchBar.addValueChangeListener((event) ->
                configurator.getDataSource().addSearchFilter(event.getValue().toString())
        );
    }

    private SearchBar.SearchBarConfigurator createSearchBarConfigurator(){
        return new SearchBar.SearchBarConfigurator()
                .setHideAddNewButton(configurator.isEmbedded() ? true : false);
    }

    public Grid<T> getGrid() {
        return configurator.getDataSource().getGrid();
    }

    public String[] propertyKeys(T item){
        if (editor == null) return new String[0];
        return editor.propertyKeys(item);
    }

    public Configurator getConfigurator(){return configurator;}

}
