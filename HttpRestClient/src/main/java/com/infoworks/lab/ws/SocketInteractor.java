package com.infoworks.lab.ws;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.template.ConfigurableInteractor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface SocketInteractor extends ConfigurableInteractor {

    default void enableHeartbeat(long[] heartbeat){}
    void setAuthorizationHeader(String token);
    void setQueryParam(String query, String param);
    void connect(String url, long timeoutInSeconds)
            throws ExecutionException, InterruptedException, TimeoutException;
    void disconnect();
    boolean reconnect();
    boolean isConnected();
    void connectionErrorHandler(Consumer<Throwable> error);
    void connectionAcceptedHandler(BiConsumer<Object, Object> afterConnect);

    <T extends Message> void subscribe(String topic
            , Class<T> type
            , Consumer<T> consumer);
    void unsubscribe(String topic);
    <T extends Message> void send(String to, T message);

}
