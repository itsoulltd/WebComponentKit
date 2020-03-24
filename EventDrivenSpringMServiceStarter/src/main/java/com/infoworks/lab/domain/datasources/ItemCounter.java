package com.infoworks.lab.domain.datasources;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadSafe Counter:
 */
public class ItemCounter {

    private AtomicInteger itemCount;
    public int getCount(){
        return itemCount.get();
    }

    private String uuid;
    public String getUuid() {
        if (uuid == null || uuid.isEmpty()){
            synchronized (this){
                uuid = UUID.randomUUID().toString();
            }
        }
        return uuid;
    }

    public ItemCounter(int initialValue) {
        this.itemCount = new AtomicInteger(initialValue);
    }

    public ItemCounter(String uuid, int initialValue) {
        this(initialValue);
        this.uuid = uuid;
    }

    public int increment(){
        return itemCount.incrementAndGet();
    }

    public int decrement(){
        if(itemCount.get() > 0)
            return itemCount.decrementAndGet();
        return itemCount.get();
    }
}
