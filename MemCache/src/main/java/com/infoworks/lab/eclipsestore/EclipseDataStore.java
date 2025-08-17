package com.infoworks.lab.eclipsestore;

import com.it.soul.lab.data.base.DataStorage;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.eclipse.serializer.afs.types.ADirectory;
import org.eclipse.serializer.reference.Lazy;
import org.eclipse.serializer.reference.LazyReferenceManager;
import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class EclipseDataStore<Key, Value> extends SimpleDataSource<Key, Value> implements DataStorage, AutoCloseable {

    private static Logger LOG = Logger.getLogger(EclipseDataStore.class.getSimpleName());
    private final EmbeddedStorageManager storage;
    private final boolean enableLazyLoad;
    private String location;
    private RootObject<Key, Value> rootObject;
    private ExecutorService executors;

    public EclipseDataStore(EmbeddedStorageManager storage, boolean enableLazyLoad, Duration lazyEvictTimeout) {
        this.enableLazyLoad = enableLazyLoad;
        if (enableLazyLoad && !lazyEvictTimeout.isZero() && !lazyEvictTimeout.isNegative()) {
            LazyRootObject.setLazyRefManager(lazyEvictTimeout);
            LOG.info("Setting up custom LazyReferenceManager was successful.");
        }
        this.storage = storage;
        boolean restored = retrieve();
        if (restored) LOG.info("Restore from storage is successful.");
        else LOG.info("Initialization of storage is successful.");
    }

    public EclipseDataStore(ADirectory location, boolean enableLazyLoad, Duration lazyEvictTimeout) {
        this(EmbeddedStorage.start(location), enableLazyLoad, lazyEvictTimeout);
        this.location = location.toPathString();
    }

    public EclipseDataStore(ADirectory location, boolean enableLazyLoad) {
        this(location, enableLazyLoad, Duration.ofMinutes(0));
    }

    public EclipseDataStore(ADirectory location) { this(location, false); }

    public EclipseDataStore(String location, boolean enableLazyLoad, Duration lazyEvictTimeout) {
        this(EmbeddedStorage.start(Paths.get(location)), enableLazyLoad, lazyEvictTimeout);
        this.location = location;
    }

    public EclipseDataStore(String location, boolean enableLazyLoad) {
        this(location, enableLazyLoad, Duration.ofMinutes(0));
    }

    public EclipseDataStore(String location) { this(location, false); }

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

        public static void setLazyRefManager(Duration lazyEvictTimeout) {
            LazyReferenceManager.set(LazyReferenceManager.New(
                    Lazy.Checker(lazyEvictTimeout.toMillis(), 0.75)
            ));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
}
