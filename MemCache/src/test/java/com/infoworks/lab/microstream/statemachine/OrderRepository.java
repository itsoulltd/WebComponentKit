package com.infoworks.lab.microstream.statemachine;

import com.infoworks.lab.microstream.MicroDataStore;
import com.it.soul.lab.data.simple.SimpleDataSource;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderRepository implements AutoCloseable {

    private static Logger LOG = Logger.getLogger("OrderRepository");
    private final SimpleDataSource<String, Order> storage;

    public OrderRepository(String location) {
        storage = new MicroDataStore<>(location + "/orders");
    }

    public void clear() {
        storage.clear();
    }

    public Optional<Order> findById (String id) {
        Order order = storage.read(id);
        return Optional.ofNullable(order);
    }

    public long count() {
        return storage.size();
    }

    public Order save(Order order) {
        storage.put(order.getOrderId(), order);
        return order;
    }

    public Order delete(Order order) {
        return storage.remove(order.getOrderId());
    }

    @Override
    public void close() throws Exception {
        if (storage instanceof MicroDataStore){
            try {
                ((MicroDataStore<String, Order>) storage).close();
                LOG.info("InMem-Order Storage Save Successful");
            } catch (Exception e) {
                LOG.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
}
