/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.infoworks.lab.components.crud.components.editor;

import com.infoworks.lab.components.crud.components.views.ConfirmationDialog;
import com.infoworks.lab.components.crud.converters.StringToNullObjectConverter;
import com.it.soul.lab.sql.entity.EntityInterface;
import com.it.soul.lab.sql.entity.Ignore;
import com.it.soul.lab.sql.entity.PrimaryKey;
import com.it.soul.lab.sql.query.models.DataType;
import com.it.soul.lab.sql.query.models.Property;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.converter.*;
import com.vaadin.flow.shared.Registration;

import javax.persistence.GeneratedValue;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Abstract base class for dialogs adding, editing or deleting items.
 *
 * Subclasses are expected to
 * <ul>
 * <li>add, during construction, the needed UI components to
 * {@link #getFormLayout()} and bind them using {@link #getBinder()}, as well
 * as</li>
 * <li>override {@link #confirmDelete()} to open the confirmation dialog with
 * the desired message (by calling
 * {@link #openConfirmationDialog(String, String, String)}.</li>
 * </ul>
 *
 * @param <T>
 *            the type of the item to be added, edited or deleted
 */
public abstract class AbstractBeanEditor<T extends EntityInterface>
        extends VerticalLayout implements BeanEditor<T> {

    /**
     * The operations supported by this dialog. Delete is enabled when editing
     * an already existing item.
     */
    public enum Operation {
        ADD("New", "add", false), EDIT("Edit", "edit", true);

        private final String nameInTitle;
        private final String nameInText;
        private final boolean deleteEnabled;

        Operation(String nameInTitle, String nameInText,
                boolean deleteEnabled) {
            this.nameInTitle = nameInTitle;
            this.nameInText = nameInText;
            this.deleteEnabled = deleteEnabled;
        }

        public String getNameInTitle() {
            return nameInTitle;
        }

        public String getNameInText() {
            return nameInText;
        }

        public boolean isDeleteEnabled() {
            return deleteEnabled;
        }
    }

    private final H3 titleField = new H3();
    private final Button saveButton = new Button("Save");
    private final Button clearButton = new Button("Clear");
    private final Button deleteButton = new Button("Delete");
    private Registration registrationForSave;
    private Registration saveShortcutRegistration;
    private Registration deleteShortcutRegistration;

    private final FormLayout formLayout = new FormLayout();
    private final HorizontalLayout buttonBar = new HorizontalLayout(saveButton,
            clearButton, deleteButton);

    private T currentItem;

    private final ConfirmationDialog<T> confirmationDialog = new ConfirmationDialog<>();

    private final String itemType;
    private BiConsumer<T, Operation> itemSaver;
    private Consumer<T> itemDeleter;

    /**
     * Constructs a new instance.
     *
     * @param itemType
     *            The readable name of the item type
     * @param itemSaver
     *            Callback to save the edited item
     * @param itemDeleter
     *            Callback to delete the edited item
     */
    protected AbstractBeanEditor(String itemType,
                                 BiConsumer<T, Operation> itemSaver, Consumer<T> itemDeleter) {
        this.itemType = itemType;
        this.itemSaver = itemSaver;
        this.itemDeleter = itemDeleter;

        initTitle();
        initFormLayout();
        initButtonBar();
    }

    private void initTitle() {
        add(titleField);
    }

    private void initFormLayout() {
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("25em", 2));
        Div div = new Div(formLayout);
        div.addClassName("has-padding");
        add(div);
    }

    private void initButtonBar() {
        saveButton.setAutofocus(shouldSaveAutofocus());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        clearButton.addClickListener(e -> clear());
        deleteButton.addClickListener(e -> deleteClicked());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonBar.setClassName("buttons");
        buttonBar.setSpacing(true);
        add(buttonBar);
    }

    protected boolean shouldSaveAutofocus(){return true;}

    /**
     * Gets the form layout, where additional components can be added for
     * displaying or editing the item's properties.
     *
     * @return the form layout
     */
    protected final FormLayout getFormLayout() {
        return formLayout;
    }

    /**
     * Gets the binder.
     *
     * @return the binder
     */
    protected abstract Binder<T> getBinder();

    /**
     * Gets the item currently being edited.
     *
     * @return the item currently being edited
     */
    protected final T getCurrentItem() {
        return currentItem;
    }

    public void addSaveClickListener(BiConsumer<T, Operation> itemSaver) {
        this.itemSaver = itemSaver;
    }

    public void addDeleteClickListener(Consumer<T> itemDeleter) {
        this.itemDeleter = itemDeleter;
    }

    public void addClearClickListener(ComponentEventListener<ClickEvent<Button>> listener) {
        clearButton.addClickListener(listener);
    }

    public final void setClearButtonTitle(String text) {
        clearButton.setText(text);
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
    public final void prepare(T item, Operation operation) {
        if (configureForm(item)) {
            currentItem = item;

            titleField.setText(operation.getNameInTitle() + " " + itemType);
            if (registrationForSave != null) {
                registrationForSave.remove();
            }
            registrationForSave = saveButton
                    .addClickListener(e -> saveClicked(operation));
            getBinder().readBean(getCurrentItem());

            deleteButton.setEnabled(operation.isDeleteEnabled());

            enableShortcuts();
        }
    }

    private void saveClicked(Operation operation) {
        boolean isValid = getBinder().writeBeanIfValid(getCurrentItem());

        if (isValid) {
            itemSaver.accept(getCurrentItem(), operation);
            close();
        } else {
            BinderValidationStatus<T> status = getBinder().validate();
        }
    }

    private void deleteClicked() {
        if (confirmationDialog.getElement().getParent() == null) {
            getUI().ifPresent(ui -> ui.add(confirmationDialog));
        }
        confirmDelete();
    }

    protected abstract void confirmDelete();

    /**
     * Opens the confirmation dialog before deleting the current item.
     *
     * The dialog will display the given title and message(s), then call
     * {@link #deleteConfirmed(EntityInterface)} if the Delete button is clicked.
     *
     * @param title
     *            The title text
     * @param message
     *            Detail message (optional, may be empty)
     * @param additionalMessage
     *            Additional message (optional, may be empty)
     */
    protected final void openConfirmationDialog(String title, String message,
            String additionalMessage) {
        disableShortcuts();
        confirmationDialog.open(title, message, additionalMessage, "Delete",
                true, getCurrentItem(), this::deleteConfirmed,
                this::deleteCancelled);
    }

    /**
     * Removes the {@code item} from the backend and close the dialog.
     *
     * @param item
     *            the item to delete
     */
    protected void doDelete(T item) {
        itemDeleter.accept(item);
        close();
    }

    protected void deleteConfirmed(T item) {
        doDelete(item);
    }

    protected void deleteCancelled() {
        enableShortcuts();
    }

    protected void enableShortcuts() {
        disableShortcuts();
        saveShortcutRegistration = saveButton.addClickShortcut(Key.ENTER);
        if (deleteButton.isEnabled()) {
            deleteShortcutRegistration =
                    deleteButton.addClickShortcut(Key.DELETE);
        }
    }

    protected void disableShortcuts() {
        if (saveShortcutRegistration != null) {
            saveShortcutRegistration.remove();
            saveShortcutRegistration = null;
        }
        if (deleteShortcutRegistration != null) {
            deleteShortcutRegistration.remove();
            deleteShortcutRegistration = null;
        }
    }

    public void close() {
        disableShortcuts();
    }

    @Override
    public void clear() {
        getFormLayout().getChildren()
                .filter(component -> component instanceof TextField)
                .forEach(component -> ((TextField) component).setValue(""));
    }

    protected final boolean configureForm(T item){
        if (Objects.nonNull(item)) {
            getFormLayout().removeAll();
            List<Property> properties = formProperties(item);
            for (Property prop : properties) {
                getBinder().removeBinding(prop.getKey());
                HasValue hasValue = getValueField(prop);
                Converter converter = getValueConverter(prop);
                if (hasValue != null){
                    if (converter != null){
                        getBinder()
                                .forField(hasValue)
                                .withConverter(converter)
                                .bind(prop.getKey());
                    }else {
                        getBinder()
                                .forField(hasValue)
                                .bind(prop.getKey());
                    }
                    getFormLayout().add((Component) hasValue);
                }
            }
        }
        return Objects.nonNull(item);
    }

    protected Converter getValueConverter(Property prop) {
        switch (prop.getType()){
            case BOOL:
                return new StringToBooleanConverter("Bool Value!");
            case INT:
                return new StringToIntegerConverter(0,"Integer Value!");
            case FLOAT:
                return new StringToFloatConverter(0.0f,"Float Value!");
            case DOUBLE:
                return new StringToDoubleConverter(0.0d,"Double Value!");
            case LONG:
                return new StringToLongConverter(0l,"Long Value!");
            case BIG_DECIMAL:
                return new StringToBigDecimalConverter(BigDecimal.ZERO,"BigDecimal Value!");
            case SQLDATE:
            case SQLTIMESTAMP:
                return new LocalDateToDateConverter();
        }
        if (prop.getType() == DataType.NULL_OBJECT){
            return new StringToNullObjectConverter();
        }
        return null;
    }

    protected HasValue getValueField(Property prop) {
        HasValue hasValue = null;
        if (prop.getType() == DataType.BOOL){
            Checkbox box = new Checkbox();
            box.setLabel(prop.getKey());
            if (prop.getValue() != null)
                box.setValue((Boolean) prop.getValue());
            else box.setIndeterminate(true);
            hasValue = box;
        }else if (prop.getType() == DataType.SQLDATE){
            DatePicker datePicker = new DatePicker();
            datePicker.setValue(LocalDate.now());
            datePicker.setClearButtonVisible(true);
            hasValue = datePicker;
        }else if(prop.getType() == DataType.SQLTIMESTAMP) {
            TimePicker timePicker = new TimePicker();
            timePicker.setValue(LocalTime.now());
            timePicker.setClearButtonVisible(true);
            hasValue = timePicker;
        }else {
            TextField field = new TextField();
            field.setLabel(prop.getKey());
            hasValue = field;
        }
        return hasValue;
    }

    protected final List<Property> formProperties(T item){
        if (!Objects.nonNull(item)) return new ArrayList<>();
        List<Property> properties = new ArrayList<>();
        try {
            Field[] fields = item.getDeclaredFields(false);
            for (Field field: fields) {
                if (skipField(field)) {
                    field.setAccessible(true);
                    String actualKey = field.getName();
                    Object value = field.get(item);
                    properties.add(new Property(actualKey, value));
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public String[] propertyKeys(T item){
        String[] keys = formProperties(item)
                .stream()
                .map(property -> property.getKey()).collect(Collectors.toList())
                .toArray(new String[0]);
        return keys;
    }

    protected boolean skipField(Field field){
        boolean isPrimaryWithAuto = field.isAnnotationPresent(PrimaryKey.class)
                && (field.getAnnotation(PrimaryKey.class)).auto();
        boolean shouldIgnored = field.isAnnotationPresent(Ignore.class);
        boolean isGeneratedPresent = field.isAnnotationPresent(GeneratedValue.class);
        return (!isPrimaryWithAuto && !shouldIgnored && !isGeneratedPresent);
    }
}
