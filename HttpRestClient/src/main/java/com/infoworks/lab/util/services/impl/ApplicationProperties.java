package com.infoworks.lab.util.services.impl;

import com.infoworks.lab.util.services.iProperties;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ApplicationProperties implements iProperties {

    private boolean createIfNotExist() throws RuntimeException{
        if (path == null) return false;
        if (!Files.exists(path)){
            File props = path.toFile();
            try {
                return props.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return false;
    }

    private Path path;
    private final Properties configProp = new Properties();

    public ApplicationProperties(String name) throws RuntimeException {
        this(name, new HashMap<>());
    }

    public ApplicationProperties(String name, Map<String, String> defaultConfig) throws RuntimeException {
        if (!name.endsWith(".properties")){
            throw new RuntimeException("Doesn't have file extension as properties");
        }
        this.path = Paths.get(name);
        if(createIfNotExist()) System.out.println("File Created!");
        load(defaultConfig);
    }

    private void load(Map<String, String> defaultConfig) throws RuntimeException {
        if (path == null) return;
        //Private constructor to restrict new instances
        System.out.println("Reading all properties from the file: " + path.toAbsolutePath().toString());
        try(InputStream in = new FileInputStream(path.toFile())) {
            configProp.load(in);
            if (configProp.isEmpty()
                    && (defaultConfig != null && !defaultConfig.isEmpty())){
                configProp.putAll(defaultConfig);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void flush() {
        if (path == null) return;
        try(OutputStream stream = new FileOutputStream(path.toFile())) {
            configProp.store(stream,"Properties file updated: " + path.toAbsolutePath().toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(boolean async) {
        flush();
    }

    public String read(String key) {
        return configProp.getProperty(key, "");
    }

    @Override
    public void put(String key, String value) {
        configProp.setProperty(key, value);
    }

    @Override
    public String replace(String key, String value) {
        return (String) configProp.replace(key, value);
    }

    @Override
    public String remove(String key) {
        return (String) configProp.remove(key);
    }

    public String fileName() {
        if (path == null) return "";
        return path.getFileName().toString();
    }

    @Override
    public int size() {
        return configProp.size();
    }

    @Override
    public boolean contains(String value) {
        return configProp.contains(value);
    }

    @Override
    public boolean containsKey(String key) {
        return configProp.containsKey(key);
    }

    @Override
    public void clear() {
        configProp.clear();
    }
}
