package com.infoworks.lab.rest.repository;

import com.infoworks.lab.rest.models.ItemCount;
import com.it.soul.lab.sql.entity.Entity;

import java.util.List;

public interface iRepository<E extends Entity, ID> {

    Class<E> getEntityType();
    String getPrimaryKeyName();
    ItemCount rowCount() throws RuntimeException;

    /**
     * @param offset
     * @param limit
     * @return
     * @throws RuntimeException
     */
    List<E> fetch(Integer offset, Integer limit) throws RuntimeException;

    /**
     * @param entity
     * @return
     * @throws RuntimeException
     */
    E insert(E entity) throws RuntimeException;

    /**
     * @param entity
     * @param id
     * @return
     * @throws RuntimeException
     */
    E update(E entity, ID id) throws RuntimeException;

    /**
     * @param id
     * @return
     * @throws RuntimeException
     */
    boolean delete(ID id) throws RuntimeException;
}
