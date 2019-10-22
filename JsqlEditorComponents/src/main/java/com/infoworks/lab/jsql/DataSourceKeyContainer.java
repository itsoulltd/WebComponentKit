package com.infoworks.lab.jsql;

import java.util.HashMap;
import java.util.Map;

public class DataSourceKeyContainer {

    public enum Keys{
        URL(null),
        DRIVER("com.mysql.jdbc.Driver"),
        SCHEMA("jdbc:mysql://"),
        USERNAME("root"),
        PASSWORD("root"),
        HOST("localhost"),
        PORT("3306"),
        NAME(null),
        QUERY("");

        private String value;
        Keys(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
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

}
