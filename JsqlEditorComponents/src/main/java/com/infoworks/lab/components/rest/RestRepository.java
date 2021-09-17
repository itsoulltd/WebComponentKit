package com.infoworks.lab.components.rest;

import com.infoworks.lab.rest.models.ItemCount;
import com.it.soul.lab.sql.entity.Entity;

import java.util.List;

public interface RestRepository<E extends Entity, ID> {
    String getPrimaryKeyName();
    Class<E> getEntityType();
    ItemCount rowCount() throws RuntimeException;
    List<E> fetch(Integer page, Integer limit) throws RuntimeException;
    E insert(E entity) throws RuntimeException;
    E update(E entity, ID id) throws RuntimeException;
    boolean delete(ID id) throws RuntimeException;
}
