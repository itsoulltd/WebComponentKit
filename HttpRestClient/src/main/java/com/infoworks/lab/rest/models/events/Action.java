package com.infoworks.lab.rest.models.events;

public interface Action<T> {
    void execute(T... any);
}
