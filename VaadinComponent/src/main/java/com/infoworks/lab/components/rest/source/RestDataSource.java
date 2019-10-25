package com.infoworks.lab.components.rest.source;

import com.infoworks.lab.components.db.source.SqlDataSource;
import com.infoworks.lab.components.rest.RestEntity;

public class RestDataSource<E extends RestEntity> extends SqlDataSource<E> {

    @Override
    public int getRowCount() {
        return super.getRowCount();
    }

}
