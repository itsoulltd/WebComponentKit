package com.infoworks.lab.rest.repository;

import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.sql.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public interface RestRepository<E extends Entity, ID> extends iRepository<E, ID> {
    default List<E> search(SearchQuery searchQuery) {return new ArrayList<>();}
}
