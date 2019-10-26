package com.infoworks.lab.mock;

import com.it.soul.lab.sql.entity.EntityInterface;

@FunctionalInterface
public interface Mockitor<P extends EntityInterface> {
    P accept();
}
