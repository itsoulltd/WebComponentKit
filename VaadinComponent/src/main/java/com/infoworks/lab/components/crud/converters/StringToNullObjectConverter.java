package com.infoworks.lab.components.crud.converters;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class StringToNullObjectConverter implements Converter<String, Object> {

    @Override
    public Result<Object> convertToModel(String value, ValueContext valueContext) {
        if (value == null || value.isEmpty())
            return Result.ok(null);
        return Result.ok(value);
    }

    @Override
    public String convertToPresentation(Object o, ValueContext valueContext) {
        return "";
    }
}
