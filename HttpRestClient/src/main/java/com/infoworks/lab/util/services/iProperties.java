package com.infoworks.lab.util.services;

import com.infoworks.lab.util.services.impl.ApplicationProperties;
import com.it.soul.lab.data.base.DataSource;
import com.it.soul.lab.data.base.DataStorage;
import com.it.soul.lab.sql.entity.EntityInterface;

import java.io.IOException;
import java.util.Map;

public interface iProperties extends DataSource<String, String>, DataStorage {

    static iProperties create(String name, Map<String, String> defaultSet) {
        return new ApplicationProperties(name, defaultSet);
    }

    void flush();
    String fileName();
    <E extends EntityInterface> void putObject(String key, E value) throws IOException;
    <E extends EntityInterface> E getObject(String key, Class<E> type) throws IOException;
}
