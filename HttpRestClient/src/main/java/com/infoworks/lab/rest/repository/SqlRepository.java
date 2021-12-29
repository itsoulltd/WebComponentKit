package com.infoworks.lab.rest.repository;

import com.it.soul.lab.sql.SQLExecutor;
import com.it.soul.lab.sql.entity.Entity;

public interface SqlRepository<E extends Entity, ID> extends iGenericRepository<E, ID> {
    SQLExecutor getExecutor();
}
