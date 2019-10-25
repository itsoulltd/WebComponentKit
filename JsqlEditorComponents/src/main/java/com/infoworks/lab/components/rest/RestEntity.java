package com.infoworks.lab.components.rest;

import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.entity.Entity;

import java.sql.SQLException;

public class RestEntity extends Entity {

    @Override
    public Boolean insert(QueryExecutor exe, String... keys) throws SQLException {
        return super.insert(exe, keys);
    }

    @Override
    public Boolean update(QueryExecutor exe, String... keys) throws SQLException {
        return super.update(exe, keys);
    }

    @Override
    public Boolean delete(QueryExecutor exe) throws SQLException {
        return super.delete(exe);
    }

}
