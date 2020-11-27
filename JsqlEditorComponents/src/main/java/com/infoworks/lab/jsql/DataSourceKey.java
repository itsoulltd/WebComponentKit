package com.infoworks.lab.jsql;

import java.util.HashMap;
import java.util.Map;

public class DataSourceKey {

    public enum Keys{
        URL(null),
        DRIVER("com.mysql.jdbc.Driver"),
        SCHEMA("jdbc:mysql://"),
        USERNAME("root"),
        PASSWORD(null),
        HOST("localhost"),
        PORT("3306"),
        NAME(null),
        QUERY("");

        private String value;
        Keys(String value) {
            this.value = value;
        }

        public String defaultValue() {
            return value;
        }
    }

    private Map<String, String> keyMap = new HashMap<>();

    public void set(Keys key, String value){
        keyMap.put(key.name(), value);
    }

    public String get(Keys key){
        return keyMap.get(key.name());
    }

    public static DataSourceKey createDataSourceKey(String suffix) {
        if (suffix == null || suffix.isEmpty()) suffix = "app.db";
        Map<String, String> env = System.getenv();
        DataSourceKey container = new DataSourceKey();
        container.set(DataSourceKey.Keys.URL, env.get(suffix + ".url"));
        container.set(DataSourceKey.Keys.DRIVER, env.get(suffix + ".driver-class-name"));
        container.set(DataSourceKey.Keys.SCHEMA, env.get(suffix + ".schema"));
        container.set(DataSourceKey.Keys.USERNAME, env.get(suffix + ".username"));
        container.set(DataSourceKey.Keys.PASSWORD, env.get(suffix + ".password"));
        container.set(DataSourceKey.Keys.HOST, env.get(suffix + ".host"));
        container.set(DataSourceKey.Keys.PORT, env.get(suffix + ".port"));
        container.set(DataSourceKey.Keys.NAME, env.get(suffix + ".name"));
        container.set(DataSourceKey.Keys.QUERY, env.get(suffix + ".query"));
        return container;
    }

}
