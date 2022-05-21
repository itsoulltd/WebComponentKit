package com.infoworks.lab.util.services;

import com.infoworks.lab.util.services.impl.ApplicationProperties;
import com.it.soul.lab.data.base.DataSource;
import com.it.soul.lab.data.base.DataStorage;

import java.util.Map;

public interface iProperties extends DataSource<String, String>, DataStorage {

    static iProperties create(String name, Map<String, String> defaultSet) {
        return new ApplicationProperties(name, defaultSet);
    }

    void flush();
    String fileName();
}
