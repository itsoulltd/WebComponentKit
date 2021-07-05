package com.infoworks.lab.simulator;

import com.it.soul.lab.sql.entity.Entity;

import java.util.Iterator;
import java.util.Map;

public class JsonLogWriter extends LogWriter{

    public JsonLogWriter(Class type) { super(type); }

    public JsonLogWriter(Class type, String fileName) {
        super(type, fileName);
    }

    public void write(Entity message){
        Map data = message.marshallingToMap(true);
        StringBuilder builder = new StringBuilder("{");
        Iterator<Map.Entry> iterator = data.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> entry = iterator.next();
            builder.append(String.format("\"%s\":\"%s\",", entry.getKey(), ((entry.getValue() != null) ? entry.getValue().toString() : "")));
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("}");
        write(builder.toString());
    }

}
