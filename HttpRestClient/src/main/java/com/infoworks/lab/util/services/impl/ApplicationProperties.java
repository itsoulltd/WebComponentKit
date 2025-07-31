package com.infoworks.lab.util.services.impl;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.util.services.iProperties;
import com.it.soul.lab.sql.entity.EntityInterface;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

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

    @Override
    public <E extends EntityInterface> void putObject(String key, E value) throws IOException{
        String json = Message.marshal(value);
        String base64 = Base64.getEncoder().encodeToString(json.getBytes());
        put(key, base64);
    }

    @Override
    public <E extends EntityInterface> E getObject(String key, Class<E> type) throws IOException{
        String base64 = read(key);
        String json = new String(Base64.getDecoder().decode(base64));
        if (!Message.isValidJson(json)) throw new IOException("Invalid Json Format!");
        return Message.unmarshal(type, json);
    }

    @Override
    public String[] readSync(int offset, int pageSize) {
        //Validation:
        int size = size();
        int fromIndex = Math.abs(offset);
        if (fromIndex >= size) return new String[0];
        int toIndex = Math.abs(offset) + Math.abs(pageSize);
        if (toIndex > size) toIndex = size;
        //In-Memory-Pagination:
        String[] values = configProp.values().toArray(new String[0]);
        List<String> items = Arrays.asList(values).subList(fromIndex, toIndex);
        return items.toArray(new String[0]);
    }

    @Override
    public void readAsync(int offset, int pageSize, Consumer<String[]> consumer) {
        if(consumer != null) {
            consumer.accept(readSync(offset, pageSize));
        }
    }
}
