package com.infoworks.lab.client.spring;

import com.infoworks.lab.mock.Mockitor;
import com.infoworks.lab.rest.models.Message;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

public class SocketMockilate extends SocketTemplate {

    @Override
    public void setAuthorizationHeader(String s) {

    }

    @Override
    public void setQueryParam(String s, String s1) {

    }

    @Override
    public void connect(String s, long l) throws ExecutionException, InterruptedException, TimeoutException {
        session = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public void disconnect() {
        if (session == null){return;}
        funcMapper.clear();
        session.shutdown();
        session = null;
    }

    @Override
    public void connectionErrorHandler(Consumer<Throwable> consumer) {

    }

    @Override
    public void connectionAcceptedHandler(BiConsumer<Object, Object> biConsumer) {

    }

    @Override
    public void configure(Object... objects) {
        //
    }

    private String publicChannel = "/";

    private Map<String, Consumer<? extends Message>> funcMapper = new ConcurrentHashMap<>();
    private Map<String, Class> topicTypeMap = new ConcurrentHashMap<>();

    private ExecutorService session;

    public <T extends Message> void subscribe(String topic
            , Class<T> type
            , Consumer<T> consumer){
        //
        if (session == null){
            LOG.log(Level.INFO,"Session Must not be null.");return;
        }

        if(topic.length() <= 0){
            LOG.log(Level.INFO,"Invalid topic");
            return;
        }
        //final String validTopic = validatePaths(topic).toString();
        final String mappingKey = validatePaths(topic).toString();
        funcMapper.put(mappingKey, consumer);
        topicTypeMap.put(mappingKey, type);
        //
    }

//    private String getMappingKey(@NotNull String topic) {
//        return validatePaths(topic) + "/" + UUID.randomUUID().toString();
//    }

    protected long getRandomSleepTime(){
        Random random = new Random();
        long sleepTime = 100 * random.nextInt(12) + 1;
        return sleepTime;
    }

    public <T extends Message> void send(String to, T message){
        //
        if (session == null){
            LOG.log(Level.INFO,"Session Must not be null.");return;
        }

        to = validatePaths(to).toString();
        if (to.startsWith(publicChannel) == false){
            //just append the PublicChannel to TO.
            to = validatePaths(publicChannel, to).toString();
        }
        //session.send(to, message);
        String mappingKey = topicMap.get(to);
        session.submit(() -> {
            if (funcMapper.containsKey(mappingKey)){
                Consumer<T> block = (Consumer<T>) funcMapper.get(mappingKey);
                T o = null;
                try {
                    Thread.sleep(getRandomSleepTime());
                    Class<T> type = topicTypeMap.get(mappingKey);
                    //Generation Interface:
                    Mockitor<T> mock = mockitorMap.get(mappingKey);
                    o = mock.accept();
                    //
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                block.accept(o);
            }
        });
    }

    @Override
    protected String domain() throws MalformedURLException {
        return null;
    }

    private Map<String, String> topicMap = new ConcurrentHashMap<>();

    public void registerTopicToDestination(String topic, String destination){
        topicMap.put(validatePaths(destination).toString(), validatePaths(topic).toString());
    }

    private Map<String, Mockitor> mockitorMap = new ConcurrentHashMap<>();

    public void registerTopicToMockitor(String topic, Mockitor<? extends Message> mockitor){
        mockitorMap.put(validatePaths(topic).toString(), mockitor);
    }

}
