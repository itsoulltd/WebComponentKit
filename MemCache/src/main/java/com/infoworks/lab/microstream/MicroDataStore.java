package com.infoworks.lab.microstream;

import com.it.soul.lab.data.base.DataStorage;
import com.it.soul.lab.data.simple.SimpleDataSource;
import one.microstream.storage.embedded.types.EmbeddedStorage;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MicroDataStore<Key, Value> extends SimpleDataSource<Key, Value> implements DataStorage, AutoCloseable {

    private static Logger LOG = Logger.getLogger(MicroDataStore.class.getSimpleName());
    private final String uuid;
    private final EmbeddedStorageManager storage;
    private Map<Key, Value> inMemStorage = new ConcurrentHashMap<>();
    private ExecutorService executors;

    public MicroDataStore(String location) {
        this.uuid = location;
        this.storage   = EmbeddedStorage.start(Paths.get(location));
        boolean restored = retrieve();
        if (restored) LOG.info("Storage Restore Successful: @" + location);
        else LOG.info("Storage Initialization Successful: @" + location);
    }

    @Override
    protected Map<Key, Value> getInMemoryStorage() {
        return inMemStorage;
    }

    protected ExecutorService getExecutors() {
        if (executors == null)
            executors = Executors.newSingleThreadExecutor();
        return executors;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void save(boolean async) {
        if(async) getExecutors().submit(() -> storage.store(inMemStorage));
        else storage.store(inMemStorage);
    }

    @Override
    public boolean retrieve() {
        if (storage.root() == null) {
            storage.setRoot(inMemStorage);
            storage.storeRoot();
            return false;
        } else {
            inMemStorage = (Map<Key, Value>) storage.root();
            return true;
        }
    }

    @Override
    public boolean delete() {
        clear();
        save(false);
        return true;
    }

    @Override
    public void close() throws Exception {
        if (storage.isRunning()) {
            save(false);
            storage.shutdown();
        }
    }
}
