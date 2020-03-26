package com.infoworks.lab.client.spring;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.template.AbstractTemplate;
import com.infoworks.lab.ws.SocketInteractor;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SocketTemplate extends AbstractTemplate implements SocketInteractor {

    protected Logger LOG = Logger.getLogger(this.getClass().getSimpleName());
    private WebSocketStompClient stompClient;
    private StompSession session;
    private String publicChannel = "/";
    private Map<String, String> queryParams = new HashMap<>();

    private void configureTemplate(SocketType type){
        if (type == SocketType.SockJS){
            WebSocketClient webSocketClient = new StandardWebSocketClient();
            List<Transport> transports = new ArrayList<>(1);
            transports.add(new WebSocketTransport(webSocketClient));
            SockJsClient sockJsClient = new SockJsClient(transports);
            stompClient = new WebSocketStompClient(sockJsClient);
        }else{
            WebSocketClient webSocketClient = new StandardWebSocketClient();
            stompClient = new WebSocketStompClient(webSocketClient);
        }
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        //stompClient.setTaskScheduler(taskScheduler);
    }

    public SocketTemplate() {}

    @Override
    protected String domain() throws MalformedURLException {
        return null;
    }

    public SocketTemplate(SocketType type) {
        configureTemplate(type);
    }

    @Override
    public void configure(Object... config) throws InstantiationException{
        if (config.length <= 0) throw new InstantiationException();
        Arrays.stream(config).forEach(o -> {
            if (o instanceof SocketType){
                configureTemplate((SocketType) o);
            }
        });
    }

    public void connect(String url)  throws ExecutionException, InterruptedException {
        try {
            connect(url,0);
        } catch (TimeoutException e) {
            //Logging
        }
    }

    public String getPublicChannel() {
        return publicChannel;
    }

    public void setPublicChannel(String publicChannel) {
        this.publicChannel = publicChannel;
    }

    @Override
    public void setQueryParam(String query, String param) {
        if (query != null || query.isEmpty() == false)
            queryParams.put(query, param);
    }

    @Override
    public void setAuthorizationHeader(String token) {
        setQueryParam(HttpHeaders.AUTHORIZATION, token);
    }

    public void connect(String url, long timeoutInSeconds)  throws ExecutionException, InterruptedException, TimeoutException {
        if (session != null){return;}
        if(url.length() > 4 && (url.startsWith("ws://") || url.startsWith("wss://"))){
            if (queryParams.size() > 0) {
                StringBuffer buffer = new StringBuffer(url+"?");
                queryParams.forEach((key, value) -> {
                    buffer.append(key+"="+value+"&");
                });
                String value = buffer.toString();
                url = value.substring(0, value.length()-1);
            }
            if(adapter == null) adapter = new SessionHandlerAdapter();
            if (timeoutInSeconds <= 0) {session = stompClient.connect(url, adapter).get();}
            else {session = stompClient.connect(url, adapter).get(timeoutInSeconds, TimeUnit.SECONDS);}
        }else{
            throw new ExecutionException(new Exception("Invalid URL format"));
        }

    }

    public void disconnect(){
        super.close();
        if (session == null){return;}
        if (session.isConnected()){session.disconnect();}
        funcMapper.clear();
        session = null;
    }

    @Override
    public boolean reconnect() {
        if (session != null){
            //TODO: Have TO Think about it,
            //TODO: reconnect means, not only re-stablish old connecting and also preserving old subscriptions too.
        }
        return false;
    }

    @Override
    public boolean isConnected() {
        if (session == null){return false;}
        return session.isConnected();
    }

    private Map<String, Consumer<? extends Message>> funcMapper = new ConcurrentHashMap<>();
    private Map<String, StompSession.Subscription> subscriptionMapper = new ConcurrentHashMap<>();

    public <T extends Message> void subscribe(String topic
            , Class<T> type
            , Consumer<T> consumer){

        if (session == null){
            LOG.log(Level.INFO,"Session Must not be null.");return;
        }

        if(topic == null || topic.isEmpty()){
            LOG.log(Level.INFO,"Invalid topic");
            return;
        }
        final String validTopic = validatePaths(topic).toString();
        final String mappingKey = getMappingKey(topic);
        funcMapper.put(mappingKey, consumer);

        StompSession.Subscription subscription = session.subscribe(validTopic, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                return type;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object o) {
                if (funcMapper.containsKey(mappingKey)){
                    Consumer<T> block = (Consumer<T>) funcMapper.get(mappingKey);
                    block.accept((T) o);
                }
            }
        });
        if (subscription != null)
            subscriptionMapper.put(mappingKey, subscription);
    }

    private String getMappingKey(String topic) {
        return validatePaths(topic) + "/" + UUID.randomUUID().toString();
    }

    public void unsubscribe(String topic){
        //
        if(topic == null || topic.isEmpty()){
            LOG.log(Level.INFO,"Invalid topic");
            return;
        }
        //Because of this, when we again wants to subscribe any listener,
        //we must call subscribe again, so that new listener get assign.
        //funcMapper.remove(topic);
        //
        //Remove all keys begins with(or associated) this topic. (key : topic/random-string)
        final String validTopic = validatePaths(topic).toString();
        List<String> keys = StreamSupport.stream(funcMapper.keySet().spliterator(), false)
                .filter(topicKey -> topicKey.toLowerCase().startsWith(validTopic))
                .collect(Collectors.toList());
        keys.stream().forEach(key -> {
            funcMapper.remove(key);
            subscriptionMapper.get(key).unsubscribe();
        });
    }

    public <T extends Message> void send(String to, T message){
        if (session == null){
            LOG.log(Level.INFO,"Session Must not be null.");return;
        }

        to = validatePaths(to).toString();
        if (to.startsWith(publicChannel) == false){
            //just append the PublicChannel to TO.
            to = validatePaths(publicChannel, to).toString();
        }
        session.send(to, message);
    }

    public <T extends Message> void send(T message){
        send(publicChannel, message);
    }

    private SessionHandlerAdapter adapter = new SessionHandlerAdapter();

    @Override
    public void connectionErrorHandler(Consumer<Throwable> error) {
        adapter.setErrorHandler(error);
    }

    @Override
    public void connectionAcceptedHandler(BiConsumer<Object, Object> afterConnect) {
        adapter.setAfterConnect(afterConnect);
    }

    private class SessionHandlerAdapter extends StompSessionHandlerAdapter {

        private Consumer errorHandler = null;
        private void setErrorHandler(Consumer errorHandler) {
            this.errorHandler = errorHandler;
        }

        private BiConsumer afterConnect = null;
        private void setAfterConnect(BiConsumer afterConnect){this.afterConnect = afterConnect;}

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            LOG.log(Level.INFO,"AfterConnected SessionID:" + session.getSessionId());
            if (afterConnect != null) afterConnect.accept(session, connectedHeaders);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            if (errorHandler != null) errorHandler.accept(exception);
        }
    }

}
