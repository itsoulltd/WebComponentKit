package com.infoworks.lab.components.crud;

import com.infoworks.lab.components.crud.components.editor.BeanDialog;
import com.infoworks.lab.components.crud.components.editor.BeanEditor;
import com.infoworks.lab.components.crud.components.editor.EmbeddedBeanEditor;
import com.infoworks.lab.components.crud.components.editor.ModalBeanEditor;
import com.infoworks.lab.components.crud.components.datasource.GridDataSource;
import com.infoworks.lab.components.crud.components.utils.EditorDisplayType;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Configurator {

    private Class<? extends EntityInterface> beanType;
    private EditorDisplayType displayType = EditorDisplayType.COMBINED;
    private GridDataSource dataSource;
    private Grid.SelectionMode selectionMode = Grid.SelectionMode.SINGLE;
    private BeanEditor editor;
    private BeanDialog dialog;
    private int pageSize = 10;
    private boolean hideSearchBar; //By Default False
    private Composite<Div> searchBar;

    public Configurator(Class<? extends EntityInterface> beanType) {
        this.beanType = beanType;
    }

    public EditorDisplayType getDisplayType() {
        return displayType;
    }

    public Configurator setDisplayType(EditorDisplayType displayType) {
        this.displayType = displayType;
        return this;
    }

    public GridDataSource getDataSource() {
        return dataSource;
    }

    public Configurator setDataSource(GridDataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public Grid.SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public Configurator setSelectionMode(Grid.SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
        return this;
    }

    public boolean isDialog(){
        return displayType == EditorDisplayType.DIALOG;
    }

    public boolean isEmbedded() {
        return displayType == EditorDisplayType.EMBEDDED;
    }

    public boolean isCombined() {
        return displayType == EditorDisplayType.COMBINED;
    }

    public BeanEditor getEditor() {
        if (editor == null && getBeanType() != null)
            editor = new EmbeddedBeanEditor(getBeanType());
        return editor;
    }

    public Configurator setEditor(BeanEditor editor) {
        this.editor = editor;
        return this;
    }

    public Configurator setEditor(Class<? extends BeanEditor> type) {
        if (getBeanType() != null) {
            try {
                Constructor constructor = type.getConstructor(Class.class);
                this.editor = (BeanEditor) constructor.newInstance(getBeanType());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public BeanDialog getDialog() {
        if (dialog == null && getBeanType() != null)
            dialog = new ModalBeanEditor(getBeanType());
        return dialog;
    }

    public Configurator setDialog(BeanDialog dialog) {
        this.dialog = dialog;
        return this;
    }

    public Configurator setDialog(Class<? extends BeanEditor> type) {
        if (getBeanType() != null) {
            try {
                Constructor constructor = type.getDeclaredConstructor(Class.class);
                BeanEditor editor = (BeanEditor) constructor.newInstance(getBeanType());
                this.dialog = new ModalBeanEditor(editor);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public Class<? extends EntityInterface> getBeanType() {
        return beanType;
    }

    public int getGridPageSize() {
        return pageSize;
    }

    public Configurator setGridPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public boolean isHideSearchBar() {
        return hideSearchBar;
    }

    public Configurator setHideSearchBar(boolean hideSearchBar) {
        this.hideSearchBar = hideSearchBar;
        return this;
    }

    public Composite<Div> getSearchBar() {
        return searchBar;
    }

    public Configurator setSearchBar(Composite<Div> searchBar) {
        this.searchBar = searchBar;
        return this;
    }
}
