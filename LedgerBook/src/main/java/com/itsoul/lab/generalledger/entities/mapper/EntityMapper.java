package com.itsoul.lab.generalledger.entities.mapper;

import com.it.soul.lab.sql.entity.Entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author towhid
 * @since 19-Aug-19
 */
public interface EntityMapper<E extends Entity> extends RowMapper<E>{
    default E mapEntity(ResultSet rs) throws SQLException{
        List<E> rows = extract(rs);
        return rows.size() > 0 ? rows.get(0) : null;
    }
}
