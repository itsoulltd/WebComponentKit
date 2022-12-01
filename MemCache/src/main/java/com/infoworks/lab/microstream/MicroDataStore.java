package com.infoworks.lab.microstream;

import com.it.soul.lab.data.base.DataStorage;
import com.it.soul.lab.data.simple.SimpleDataSource;
import one.microstream.reference.Lazy;
import one.microstream.storage.embedded.types.EmbeddedStorage;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class MicroDataStore<Key, Value> extends SimpleDataSource<Key, Value> implements DataStorage, AutoCloseable {

    private static Logger LOG = Logger.getLogger(MicroDataStore.class.getSimpleName());
    private final String location;
    private final EmbeddedStorageManager storage;
    private RootObject<Key, Value> rootObject;
    private ExecutorService executors;
    private final boolean enableLazyLoad;

    public MicroDataStore(String location, boolean enableLazyLoad) {
        this.location = location;
        this.enableLazyLoad = enableLazyLoad;
        this.storage   = EmbeddedStorage.start(Paths.get(location));
        boolean restored = retrieve();
        if (restored) LOG.info("Storage Restore Successful: @" + location);
        else LOG.info("Storage Initialization Successful: @" + location);
    }

    public MicroDataStore(String location) { this(location, false); }

    @Override
    protected Map<Key, Value> getInMemoryStorage() {
        return rootObject.getMemStorage();
    }

    protected ExecutorService getExecutors() {
        if (executors == null)
            executors = Executors.newSingleThreadExecutor();
        return executors;
    }

    @Override
    public String getUuid() {
        return location;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public void save(boolean async) {
        if(async) getExecutors().submit(() -> storage.store(rootObject.getMemStorage()));
        else storage.store(rootObject.getMemStorage());
    }

    @Override
    public boolean retrieve() {
        if (Objects.nonNull(rootObject)) return false;
        if (storage.root() == null) {
            rootObject = enableLazyLoad ? new LazyRootObject(getLocation()) : new RootObject<>(getLocation());
            storage.setRoot(rootObject);
            storage.storeRoot();
            return false;
        } else {
            rootObject = (RootObject<Key, Value>) storage.root();
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

    ///////////////////////////////////////////Root-Object//////////////////////////////////////////////

    private static class RootObject<Key, Value> {

        private final String location;
        private final Map<Key, Value> memStorage;

        public RootObject(String location) {
            this.location = location;
            this.memStorage = new ConcurrentHashMap<>();
        }

        public String getLocation() {
            return location;
        }

        public Map<Key, Value> getMemStorage() {
            return memStorage;
        }
    }

    private static class LazyRootObject<Key, Value> extends RootObject<Key, Value> {

        private final Lazy<Map<Key, Value>> memStorage;

        public LazyRootObject(String location) {
            super(location);
            this.memStorage = Lazy.Reference(new ConcurrentHashMap<>());
        }

        public Map<Key, Value> getMemStorage() {
            return Lazy.get(memStorage);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
}
