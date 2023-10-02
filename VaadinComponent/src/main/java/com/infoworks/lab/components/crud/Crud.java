package com.infoworks.lab.components.crud;

import com.infoworks.lab.components.crud.components.editor.AbstractBeanEditor;
import com.infoworks.lab.components.crud.components.editor.BeanDialog;
import com.infoworks.lab.components.crud.components.editor.BeanEditor;
import com.infoworks.lab.components.crud.components.views.search.ISearchBar;
import com.infoworks.lab.components.crud.components.views.search.SearchBar;
import com.infoworks.lab.components.crud.components.views.search.SearchBarConfigurator;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Crud<T extends EntityInterface> extends Composite<Div> {

    public interface EventListener<T> {
        void onSaveSuccess(T savedItem, Object event);
        void onDeleteSuccess(T deletedItem);
    }

    protected static final Logger LOG = Logger.getLogger(Crud.class.getSimpleName());
    private Composite<Div> searchBar;
    private Configurator configurator;
    private BeanEditor editor;
    private BeanDialog dialog;
    private Component parentLayout;
    private EventListener eventListener;

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
            if (configurator.getSearchBar() != null) {
                this.searchBar = configurator.getSearchBar();
            } else {
                SearchBarConfigurator sConfig = new SearchBarConfigurator()
                        .setHideAddNewButton(configurator.isEmbedded() ? true : false);
                this.searchBar = new SearchBar(configurator.getBeanType(), sConfig);
            }
        }
        this.parentLayout = prepareParentLayout();
        getContent().add(this.parentLayout);
    }

    protected Component prepareParentLayout(){
        VerticalLayout parent = new VerticalLayout();
        parent.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        //
        if (!configurator.isHideSearchBar()
                && this.searchBar != null) {
            parent.add(this.searchBar);
            configureSearchBarEvents();
        }
        //
        parent.add(configurator.getDataSource().getGrid());
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
            LOG.log(Level.WARNING, e.getMessage(), e);
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
                //System.out.println(((T)item).marshallingToMap(false));
                LOG.log(Level.INFO, "AddSaveClick: Start");
                configurator.getDataSource().save((T)item);
                configurator.getDataSource().reloadGrid();
                try {
                    //
                    EntityInterface ein = configurator.getBeanType().newInstance();
                    editor.prepare((T) ein, AbstractBeanEditor.Operation.ADD);
                } catch (InstantiationException | IllegalAccessException e) {
                    LOG.log(Level.WARNING, e.getMessage(), e);
                }
                if (eventListener != null)
                    eventListener.onSaveSuccess(item, event);
                LOG.log(Level.INFO, "AddSaveClick: End");
            });
            //
            this.editor.addDeleteClickListener((item) -> {
                //System.out.println(((T)item).marshallingToMap(false));
                LOG.log(Level.INFO, "AddDeleteClick: Start");
                configurator.getDataSource().delete((T)item);
                configurator.getDataSource().reloadGrid();
                try {
                    EntityInterface ein = configurator.getBeanType().newInstance();
                    editor.prepare((T) ein, AbstractBeanEditor.Operation.ADD);
                } catch (InstantiationException | IllegalAccessException e) {
                    LOG.log(Level.WARNING, e.getMessage(), e);
                }
                if (eventListener != null)
                    eventListener.onDeleteSuccess(item);
                LOG.log(Level.INFO, "AddDeleteClick: End");
            });
            //
        } catch (InstantiationException e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
        }
    }

    private void configureDialogForm(VerticalLayout parent){
        this.dialog = configurator.getDialog();
        //
        this.dialog.addSaveClickListener((item, event) -> {
            //System.out.println(((T)item).marshallingToMap(false));
            LOG.log(Level.INFO, "AddSaveClick: Start");
            configurator.getDataSource().save((T)item);
            this.dialog.close();
            configurator.getDataSource().reloadGrid();
            if (eventListener != null)
                eventListener.onSaveSuccess(item, event);
            LOG.log(Level.INFO, "AddSaveClick: End");
        });
    }

    private void configureSearchBarEvents() {
        if (searchBar == null) return;
        if (searchBar instanceof ISearchBar) {
            ((ISearchBar<?>) searchBar).configureDefaultEvents(configurator, dialog);
        }
    }

    public Grid<T> getGrid() {
        return configurator.getDataSource().getGrid();
    }

    public String[] propertyKeys(T item){
        if (editor == null) return new String[0];
        return editor.propertyKeys(item);
    }

    public Configurator getConfigurator(){return configurator;}

    public Crud addEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
        return this;
    }

}
