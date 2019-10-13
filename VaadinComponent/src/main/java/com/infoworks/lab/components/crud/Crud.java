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
        configurator.getDataSource().setGrid(new Grid<>(configurator.getBeanType()));
        getGrid().setSelectionMode(configurator.getSelectionMode());
        this.searchBar = new SearchBar(configurator.getBeanType(), createSearchBarConfigurator());
        this.parentLayout = prepareParentLayout();
        getContent().add(this.parentLayout);
    }

    protected Component prepareParentLayout(){
        VerticalLayout parent = new VerticalLayout();
        parent.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        parent.add(searchBar);
        prepareGridUI(configurator.getDataSource().getGrid());
        parent.add(configurator.getDataSource().getGrid());
        //
        if (configurator.isDialog())
            configureDialogForm(parent);
        else if(configurator.isEmbedded())
            configureEmbeddedForm(parent);
        else {
            configureDialogForm(parent);
            configureEmbeddedForm(parent);
        }
        return parent;
    }

    protected void prepareGridUI(Grid grid){
        //TODO:Configure Grid Alignment and visibility respect to FormLayout:
        //grid.setSizeFull();
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
        //
        searchBar.addValueChangeListener((event) ->
            configurator.getDataSource().addSearchFilter(event.getValue().toString())
        );
        //
        this.dialog.addSaveClickListener((item, event) -> {

            System.out.println(((T)item).marshallingToMap(false));
            configurator.getDataSource().save((T)item);
            this.dialog.close();
            configurator.getDataSource().reloadGrid();
        });
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
}
