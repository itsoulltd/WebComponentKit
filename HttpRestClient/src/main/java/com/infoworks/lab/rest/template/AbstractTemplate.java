package com.infoworks.lab.rest.template;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.QueueItem;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.models.ResponseList;
import com.it.soul.lab.sql.entity.EntityInterface;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.*;
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

    protected String urlencodedQueryParam(QueryParam...params){
        if (params == null) return "";
        StringBuilder buffer = new StringBuilder();
        //Separate Paths:
        List<String> pathsBag = new ArrayList<>();
        for (QueryParam query : params) {
            if (query.getValue() != null && !query.getValue().isEmpty()) {
                continue;
            }
            pathsBag.add(query.getKey());
        }
        buffer.append(validatePaths(pathsBag.toArray(new String[0])));
        //Incorporate QueryParams:
        buffer.append("?");
        for (QueryParam query : params){
            if (query.getValue() == null || query.getValue().isEmpty()){
                continue;
            }
            try {
                buffer.append(query.getKey()
                        + "="
                        + URLEncoder.encode(query.getValue(), "UTF-8")
                        + "&");
            } catch (UnsupportedEncodingException e) {}
        }
        String value = buffer.toString();
        value = value.substring(0, value.length()-1);
        return value;
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

    protected <T extends EntityInterface> List inflateJson(String json, Class<T> type) throws IOException {
        if (json != null && !json.isEmpty()){
            if (json.startsWith("{")){
                return Arrays.asList(parse(json, type));
            }else if(json.startsWith("[")){
                List result = new ArrayList();
                List items = parse(json, ArrayList.class);
                Iterator itr = items.iterator();
                while (itr.hasNext()){
                    Object dts = itr.next();
                    if (dts instanceof Map){
                        try {
                            T instance = getJsonSerializer().convertValue(dts, type);
                            result.add(instance);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                ResponseList collection = new ResponseList<>(result);
                return Arrays.asList(collection);
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
            if (trimmed.length() > 2 && trimmed.endsWith("/"))
                trimmed = trimmed.substring(0, trimmed.length() - 1);

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
    private ExecutorService _exeService;
    protected ExecutorService getExeService(){
        if (_exeService == null){
            synchronized (this){
                _exeService = Executors.newSingleThreadExecutor();
            }
        }
        return _exeService;
    }

    protected void close(){
        if (_exeService == null) return;
        try {
            if (!_exeService.isShutdown())
                _exeService.shutdown();
        } catch (Exception e) {}
        finally {
            _exeService = null;
        }
    }

    public void notify(Object produce){
        QueueItem observer = _consumerQueue.poll();
        if (observer != null){
            //calling off the main thread.
            observer.getConsumer().accept(produce);
        }
    }

    public void addToQueue(QueueItem<? extends EntityInterface> queueItem){
        //Add to Queue
        _consumerQueue.add(queueItem);
    }

    public void addConsumer(Consumer<? extends Response> consumer){
        //Add to Queue
        _consumerQueue.add(new QueueItem(consumer));
    }

    public void submit(Runnable task){
        getExeService().submit(task);
    }

    public void execute(Runnable task){
        getExeService().execute(task);
    }

}
