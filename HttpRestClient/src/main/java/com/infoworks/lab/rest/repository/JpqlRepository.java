package com.infoworks.lab.rest.repository;

import com.it.soul.lab.jpql.service.JPQLExecutor;
import com.it.soul.lab.sql.entity.Entity;

public interface JpqlRepository<E extends Entity, ID> extends iGenericRepository<E, ID> {
    JPQLExecutor getExecutor();
}
