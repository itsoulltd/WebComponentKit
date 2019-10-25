package com.infoworks.lab.mock;

import com.it.soul.lab.sql.entity.Entity;

@FunctionalInterface
public interface Mockitor<P extends Entity> {
    P accept();
}
