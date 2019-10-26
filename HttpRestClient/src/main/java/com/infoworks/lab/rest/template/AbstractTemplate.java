package com.infoworks.lab.rest.template;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.rest.models.QueueItem;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.entity.Entity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Consumer;

public abstract class AbstractTemplate {

    abstract protected String domain() throws MalformedURLException;

    protected String routePath() {
        String routeTo = "/" + getClass().getSimpleName();
        if (getClass().isAnnotationPresent(Route.class)){
            routeTo = getClass().getAnnotation(Route.class).value();
        }
        return routeTo;
    }

    private ObjectMapper jsonSerializer;
    protected final ObjectMapper getJsonSerializer() {
        if (jsonSerializer == null) {
            jsonSerializer = new ObjectMapper();
            jsonSerializer.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            jsonSerializer.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
            jsonSerializer.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        return jsonSerializer;
    }

    protected <T extends Entity> List inflateJson(String json, Class<T> type) throws IOException {
        if (json != null && !json.isEmpty()){
            if (json.startsWith("{")){
                return Arrays.asList(parse(json, type));
            }
        }
        return null;
    }

    protected  <T> T parse(String json, Class<T> type) throws IOException {
        return getJsonSerializer().readValue(json, type);
    }

    protected  <T> T parse(String json, TypeReference<T> type) throws IOException{
        return getJsonSerializer().readValue(json, type);
    }

    protected  String parseJson(InputStream ios) throws IOException {
        int size = ios.available();
        byte[] buffer = new byte[size];
        try{
            ios.read(buffer);
        }finally {
            ios.close();
        }
        return new String(buffer, "UTF-8");
    }

    protected StringBuffer validatePaths(String... params) {
        StringBuffer buffer = new StringBuffer();
        Arrays.stream(params).forEach(str -> {
            String trimmed = str.trim();
            if (trimmed.length() > 2 && trimmed.endsWith("/")) trimmed = trimmed.substring(0, trimmed.length() - 1);
            if(trimmed.startsWith("/"))
                buffer.append(trimmed);
            else
                buffer.append("/" + trimmed);
        });
        return buffer;
    }

    public String resourcePath(String... params) throws MalformedURLException {
        List<String> items = new ArrayList<>(Arrays.asList(params));
        items.add(0, routePath());
        StringBuffer buffer = validatePaths(items.toArray(new String[0]));
        String domain = domain();
        if(domain.endsWith("/")) domain = domain.substring(0, domain.length() - 1);
        return domain + buffer.toString();
    }

    /**
     * Queue have to be ThreadSafe:
     */
    private Queue<QueueItem> _consumerQueue = new PriorityBlockingQueue<>();
    /**
     * Tasks are guaranteed to execute sequentially.
     */
    private ExecutorService _exeService = Executors.newSingleThreadExecutor();

    public void notify(Object produce){
        QueueItem observer = _consumerQueue.poll();
        if (observer != null){
            //calling off the main thread.
            observer.getConsumer().accept(produce);
        }
    }

    public void addToQueue(QueueItem<? extends Entity> queueItem){
        //Add to Queue
        _consumerQueue.add(queueItem);
    }

    public void addConsumer(Consumer<? extends Response> consumer){
        //Add to Queue
        _consumerQueue.add(new QueueItem(consumer));
    }

    public void submit(Runnable task){
        _exeService.submit(task);
    }

    public void execute(Runnable task){
        _exeService.execute(task);
    }
}
