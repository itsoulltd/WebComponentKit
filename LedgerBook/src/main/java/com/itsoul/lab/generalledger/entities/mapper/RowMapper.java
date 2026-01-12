package com.itsoul.lab.generalledger.entities.mapper;

import com.infoworks.entity.Entity;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author towhid
 * @since 19-Aug-19
 */
public interface RowMapper<R extends Entity> {
    default List<R> extract(ResultSet rs) throws SQLException{
        int index = 0;
        List<R> collection = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int numCol = rsmd.getColumnCount();
        while (rs.next()){
            try {
                R entity = mapRow(rs, index++, numCol);
                collection.add(entity);
            } catch (SQLException e) {}
        }
        return collection;
    }
    R mapRow(ResultSet rs, int rowNum, int columnCount) throws SQLException;
}
