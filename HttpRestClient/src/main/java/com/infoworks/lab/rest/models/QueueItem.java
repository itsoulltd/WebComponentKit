package com.infoworks.lab.rest.models;

import com.it.soul.lab.sql.entity.Entity;

import java.util.function.Consumer;

public class QueueItem<T extends Entity> implements Comparable{

    private Consumer<T> consumer;

    public QueueItem(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    public Consumer<T> getConsumer() {
        return consumer;
    }

    @Override
    public int compareTo(Object o) {
        //FUTURE: We are currently doing no priority:
        //Atomic static counter can be a good option: private orderID: Integer = Static.Automic.Counter++;
        //return orderID;
        return 0;
    }
}
