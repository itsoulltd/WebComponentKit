package com.infoworks.lab.components.crud.components.views.search;

import com.it.soul.lab.sql.entity.EntityInterface;
import com.it.soul.lab.sql.query.models.Property;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SearchBarConfigurator {
    private boolean hideAddNewButton;
    String[] skipProperties = new String[0];

    public boolean isHideAddNewButton() {
        return hideAddNewButton;
    }

    public SearchBarConfigurator setHideAddNewButton(boolean hideAddNewButton) {
        this.hideAddNewButton = hideAddNewButton;
        return this;
    }

    public <T extends EntityInterface> List<Property> getProperties(Class<T> type
            , String...skipKeys) {
        T item = null;
        try {
            item = type.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        if (!Objects.nonNull(item)) return new ArrayList<>();
        List<Property> properties = new ArrayList<>();
        //
        Map<String, Object> propMap = item.marshallingToMap(false);
        propMap.forEach((key, value) -> {
            if (Arrays.stream(skipKeys)
                    .noneMatch(val -> val.equalsIgnoreCase(key))) {
                properties.add(new Property(key, value));
            }
        });
        return properties;
    }

    public String[] getSkipProperties() {
        return skipProperties;
    }

    public SearchBarConfigurator setSkipProperties(String...skipProperties) {
        this.skipProperties = skipProperties;
        return this;
    }
}
