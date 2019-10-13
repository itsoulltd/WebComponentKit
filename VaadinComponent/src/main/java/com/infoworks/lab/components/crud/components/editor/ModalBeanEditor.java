package com.infoworks.lab.components.crud.components.editor;

import com.it.soul.lab.sql.entity.EntityInterface;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ModalBeanEditor<T extends EntityInterface> extends Dialog implements BeanDialog<T> {

    private final BeanEditor<T> editor;

    public BeanEditor<T> getEditor() {
        return editor;
    }

    public ModalBeanEditor(Class<T> beanType) {
        this(new EmbeddedBeanEditor(beanType));
    }

    public ModalBeanEditor(BeanEditor<T> editor) {
        this.editor = editor;
        add((Component) editor);
        editor.addSaveClickListener((item, opt) -> {
            //TODO:
            close();
        });
        editor.addDeleteClickListener((item) -> {
            //TODO:
            close();
        });
        editor.setClearButtonTitle("Close");
        editor.addClearClickListener((event) -> close());
        setCloseOnEsc(true);
        setCloseOnOutsideClick(false);
    }

    /**
     * Opens the given item for editing in the dialog.
     *
     * @param item
     *            The item to edit; it may be an existing or a newly created
     *            instance
     * @param operation
     *            The operation being performed on the item
     */
    public final void open(T item, AbstractBeanEditor.Operation operation) {
        getEditor().prepare(item, operation);
        open();
    }

    @Override
    public void close() {
        //editor.clear();
        super.close();
    }

    @Override
    public void prepare(T item, AbstractBeanEditor.Operation operation) {
        getEditor().prepare(item, operation);
    }

    @Override
    public String[] propertyKeys(T item) {
        return getEditor().propertyKeys(item);
    }

    @Override
    public void clear() {
        getEditor().clear();
    }

    @Override
    public void setClearButtonTitle(String text) {
        getEditor().setClearButtonTitle(text);
    }

    @Override
    public void addSaveClickListener(BiConsumer<T, AbstractBeanEditor.Operation> itemSaver) {
        getEditor().addSaveClickListener(itemSaver);
    }

    @Override
    public void addDeleteClickListener(Consumer<T> itemDeleter) {
        getEditor().addDeleteClickListener(itemDeleter);
    }

    @Override
    public void addClearClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        getEditor().addClearClickListener(listener);
    }

}
