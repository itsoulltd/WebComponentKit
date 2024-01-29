package com.infoworks.lab.client.spring;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.client.data.repository.DataRestRepository;
import com.infoworks.lab.client.data.rest.Any;
import com.infoworks.lab.client.data.rest.Page;
import com.infoworks.lab.client.data.rest.PaginatedResponse;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.QueryParam;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class DataRestClient<Value extends Any> extends SimpleDataSource<Object, Value> implements DataRestRepository<Value> {

    private final URL baseUrl;
    private ExecutorService service;
    private RestTemplate template;
    private PaginatedResponse baseResponse;
    private Class<? extends Any> anyClassType;
    private HttpHeaders httpHeaders;
    private boolean enableLogging;

    public DataRestClient(Class<? extends Any> type, URL baseUrl) {
        this(type, baseUrl, new RestTemplate());
    }

    public DataRestClient(Class<? extends Any> type, URL baseUrl, RestTemplate template) {
        this(type, baseUrl, template, Executors.newSingleThreadExecutor());
    }

    public DataRestClient(Class<? extends Any> type, URL baseUrl, ExecutorService service) {
        this(type, baseUrl, new RestTemplate(), service);
    }

    public DataRestClient(Class<? extends Any> type, URL baseUrl, RestTemplate template, ExecutorService service) {
        this.anyClassType = type;
        this.baseUrl = baseUrl;
        this.service = service;
        this.template = template;
    }

    protected ExecutorService getService() {
        if (service == null){
            service = Executors.newSingleThreadExecutor();
        }
        return service;
    }

    public HttpHeaders getHttpHeaders() {
        if (httpHeaders == null) httpHeaders = new HttpHeaders();
        return httpHeaders;
    }

    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public boolean isEnableLogging() {
        return enableLogging;
    }

    public void setEnableLogging(boolean enableLogging) {
        this.enableLogging = enableLogging;
    }

    @Override
    public void close() throws Exception {
        //Do all memory clean-up and terminate running process:
        clear();
        baseResponse = null;
        //immediate shutdown all enqueued tasks and return
        service.shutdown();
        service = null;
    }

    /**
     * Create Or Update an Entity from server and cache.
     * @param key
     * @param value
     * @throws RuntimeException
     */
    @Override
    public void put(Object key, Value value) throws RuntimeException {
        //Put will do PUT
        key = key.toString();
        Map<String, Object> putBody = value.marshallingToMap(true);
        HttpEntity<Map> update = new HttpEntity<>(putBody, getHttpHeaders());
        String updatePath = baseUrl.toString() + "/" + key;
        String updateResult = exchange(HttpMethod.PUT, update, updatePath);
        if(isEnableLogging()) System.out.println(updateResult);
        if(containsKey(key))
            super.replace(key, value);
    }

    /**
     * Create an Entity from server and cache.
     * @param value
     * @return
     * @throws RuntimeException
     */
    @Override
    public Object add(Value value) throws RuntimeException {
        //Add will do POST
        try {
            Map<String, Object> postBody = value.marshallingToMap(true);
            HttpEntity<Map> create = new HttpEntity<>(postBody, getHttpHeaders());
            String rootURL = baseUrl.toString();
            String result = exchange(HttpMethod.POST, create, rootURL);
            if(isEnableLogging()) System.out.println(result);
            Value created = (Value) Message.unmarshal(anyClassType, result);
            Object key = created.parseId().orElse(null);
            if(key != null) super.put(key.toString(), value);
            return key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Remove an Entity from server and cache.
     * @param key
     * @return
     * @throws RuntimeException
     */
    @Override
    public Value remove(Object key) throws RuntimeException {
        //Remove will do DELETE
        key = key.toString();
        Map<String, Object> body = new HashMap();
        HttpEntity<Map> delete = new HttpEntity<>(body, getHttpHeaders());
        String deletePath = baseUrl.toString() + "/" + key;
        String deleteResult = exchange(HttpMethod.DELETE, delete, deletePath);
        if(isEnableLogging()) System.out.println(deleteResult);
        //Now remove from local if exist in cache:
        Value any = super.remove(key);
        return any;
    }

    @Override
    public void delete(Value value) throws RuntimeException {
        this.remove(value.getId());
    }

    /**
     * Read from cache if not exist then fetch from server and update the cache.
     * @param key
     * @return
     * @throws RuntimeException
     */
    @Override
    public Value read(Object key) throws RuntimeException {
        //First check in Cache:
        key = key.toString();
        Value any = super.read(key);
        if (any != null) return any;
        //Read will do GET
        try {
            HttpHeaders headers = getHttpHeaders();
            Map<String, Object> body = new HashMap();
            HttpEntity<Map> get = new HttpEntity<>(body, headers);
            String getPath = baseUrl.toString() + "/" + key;
            String getResult = exchange(HttpMethod.GET, get, getPath);
            if(isEnableLogging()) System.out.println(getResult);
            Value value = (Value) Message.unmarshal(anyClassType, getResult);
            super.put(key, value);
            return value;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Following are new-funcs:
     */

    /**
     * execute(HttpActions)
     * @param method
     * @param entity
     * @param rootURL
     * @return
     * @throws RuntimeException
     */
    protected String exchange(HttpMethod method, HttpEntity entity, String rootURL, Object...args)
            throws RuntimeException {
        try {
            ResponseEntity<String> rs = template.exchange(rootURL, method, entity, String.class, args);
            //Checking Network Error:
            if (rs.getStatusCodeValue() >= 400)
                throw new RuntimeException( rs.getBody() + ". Status Code: " + rs.getStatusCodeValue());
            String result = rs.getBody();
            if (result == null || result.isEmpty())
                result = "Response Code: " + rs.getStatusCode();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load the baseUrl and its result into local cache.
     * If we need to re-load, then first do close() and then call load() again.
     * @return
     * @throws RuntimeException
     */
    public PaginatedResponse load() throws RuntimeException {
        if (baseResponse != null) return baseResponse;
        //Load the base URL:
        HttpHeaders headers = getHttpHeaders();
        Map body = new HashMap();
        HttpEntity<Map> entity = new HttpEntity<>(body, headers);
        String rootURL = baseUrl.toString();
        try {
            String result = exchange(HttpMethod.GET, entity, rootURL);
            if(isEnableLogging()) System.out.println(result);
            Map<String, Object> dataMap = Message.unmarshal(new TypeReference<Map<String, Object>>() {}, result);
            baseResponse = new PaginatedResponse(dataMap);
            //Parse page items and cache in local:
            parsePageItemAndCacheInMemory(dataMap);
            return baseResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load asynchronously the baseUrl and its result into local cache.
     * @param consumer
     */
    public void load(Consumer<PaginatedResponse> consumer) {
        if (consumer == null) return;
        if (baseResponse != null)
            consumer.accept(baseResponse);
        //Load the base URL:
        getService().submit(() -> {
            try {
                baseResponse = load();
                consumer.accept(baseResponse);
            } catch (RuntimeException e) {
                PaginatedResponse response = new PaginatedResponse();
                response.setError(e.getMessage());
                response.setStatus(400);
                consumer.accept(response);
            }
        });
    }

    /**
     * Fetch Next page Until the End of Line.
     * Also add paged items into local cache.
     * @return
     */
    public Optional<List<Value>> next() {
        if (isLastPage()) return Optional.ofNullable(null);
        if (baseResponse != null){
            Page page = baseResponse.getPage();
            Map<String, Object> dataMap = fetchNext(page).orElse(null);
            //Update Next page info:
            baseResponse.updatePage(dataMap);
            baseResponse.updateLinks(dataMap);
            List<Value> items = parsePageItemAndCacheInMemory(dataMap);
            return Optional.ofNullable(items);
        }
        return Optional.ofNullable(null);
    }

    private List<Value> parsePageItemAndCacheInMemory(Map<String, Object> dataMap) {
        //Parse items:
        List<Value> items = parsePageItems(dataMap);
        //Add into in-memory store:
        if (items != null || !items.isEmpty()) {
            items.forEach(item -> {
                Object key = item.parseId().orElse(null);
                if(key != null) super.put(key.toString(), item);
            });
        }
        return items;
    }

    protected List<Value> parsePageItems(Map<String, Object> dataMap) {
        List<Value> typedObjects = new ArrayList<>();
        if (dataMap == null) return typedObjects;
        //Parse DataMap to get-objects:
        Map<String, List<Map<String, Object>>> embedded =
                (Map) dataMap.get("_embedded");
        List<Map<String, Object>> objects = getCollectionResourceRel(embedded);
        if (objects == null) return typedObjects;
        //Try to re-recreate objects:
        for (Map<String, Object> entry : objects) {
            try {
                Value parsed = (Value) anyClassType.newInstance();
                parsed.unmarshallingFromMap(entry, true);
                typedObjects.add(parsed);
            } catch (InstantiationException
                     | IllegalAccessException e) {}
        }
        return typedObjects;
    }

    /**
     * Asynchronous version of next()
     * @param consumer
     */
    public void next(Consumer<Optional<List<Value>>> consumer) {
        if (consumer != null) {
            getService().submit(() -> consumer.accept(next()));
        }
    }

    /**
     * "page" : {
     *     "size" : 5,
     *     "totalElements" : 50,
     *     "totalPages" : 10,
     *     "number" : 0
     *   }
     * At the bottom is extra data about the page settings,
     * including the size of a page, total elements, total pages, and the page number you are currently viewing.
     * @return
     */
    public boolean isLastPage() {
        if (baseResponse == null) return true;
        Page current = baseResponse.getPage();
        int currentPage = current.getNumber();
        return (currentPage >= current.getTotalPages()) ? true : false;
    }

    public int currentPage() {
        return number();
    }

    public int number() {
        if (baseResponse == null) return 0;
        Page current = baseResponse.getPage();
        return current.getNumber();
    }

    public int totalPages() {
        if (baseResponse == null) return 0;
        Page current = baseResponse.getPage();
        return current.getTotalPages();
    }

    public int totalElements() {
        if (baseResponse == null) return 0;
        Page current = baseResponse.getPage();
        return current.getTotalElements();
    }

    /**
     * May return Null
     * @param current
     * @return
     */
    protected Optional<Map<String, Object>> fetchNext(Page current) {
        int currentPage = current.getNumber();
        int pageSize = current.getSize();
        int nextPage = currentPage + 1;
        //
        HttpHeaders headers = getHttpHeaders();
        Map body = new HashMap();
        HttpEntity<Map> entity = new HttpEntity<>(body, headers);
        String nextPagePath = baseUrl.toString() + "?page={page}&size={size}";
        String result = exchange(HttpMethod.GET, entity, nextPagePath, nextPage, pageSize);
        if(isEnableLogging()) System.out.println(result);
        //
        Map<String, Object> dataMap = null;
        try {
            dataMap = Message.unmarshal(
                    new TypeReference<Map<String, Object>>() {}, result);
        } catch (IOException e) {}
        return Optional.ofNullable(dataMap);
    }

    /**
     * Declared in Spring-Data-Rest repository annotation:
     * e.g. @RepositoryRestResource(collectionResourceRel = "passengers")
     * @return
     */
    protected List<Map<String, Object>> getCollectionResourceRel(Map<String, List<Map<String, Object>>> embedded) {
        if (embedded == null) return null;
        Optional<String> possibleKey = embedded.keySet().stream().findFirst();
        return possibleKey.isPresent() ? embedded.get(possibleKey.get()) : null;
    }

    /**
     * Declared in Spring-Data-Rest repository annotation:
     * e.g. @RepositoryRestResource(path = "passengers")
     * @return
     */
    protected String getApiPathName() {
        String path = baseUrl.getPath();
        String[] paths = path.split("/");
        String pathName = paths[paths.length - 1];
        return pathName;
    }

    /**
     * Search using declared func-name and search params:
     * @param function
     * @param params
     * @return
     */
    public Optional<List<Value>> search(String function, QueryParam... params) {
        if (Objects.isNull(function) || function.isEmpty()) return Optional.ofNullable(null);
        if (function.startsWith("/")) function = function.replaceFirst("/", "");
        PaginatedResponse response = load();
        Object href = response.getLinks().getSearch().get("href");
        if (href != null) {
            String searchAction = function + encodedQueryParams(params);
            HttpEntity<Map> entity = new HttpEntity(null, getHttpHeaders());
            String searchUrl = href + "/" + searchAction;
            String result = exchange(HttpMethod.GET, entity, searchUrl);
            try {
                Map<String, Object> dataMap =
                        Message.unmarshal(new TypeReference<Map<String, Object>>() {}, result);
                List<Value> items = parsePageItems(dataMap);
                return Optional.ofNullable(items);
            } catch (IOException e) {}
        }
        return Optional.ofNullable(null);
    }

    /**
     * Async version of search:
     * @param function
     * @param params
     * @param consumer
     */
    public void search(String function, QueryParam[] params, Consumer<Optional<List<Value>>> consumer) {
        if (consumer != null) {
            getService().submit(() -> consumer.accept(search(function, params)));
        }
    }

    public boolean isSearchActionExist(String function) {
        boolean outcome = false;
        if (Objects.isNull(function) || function.isEmpty()) return outcome;
        if (function.startsWith("/")) function = function.replaceFirst("/", "");
        PaginatedResponse response = load();
        Object href = response.getLinks().getSearch().get("href");
        if (href != null) {
            HttpEntity<Map> entity = new HttpEntity(null, getHttpHeaders());
            String searchUrl = href.toString();
            String result = exchange(HttpMethod.GET, entity, searchUrl);
            try {
                Map<String, Object> dataMap =
                        Message.unmarshal(new TypeReference<Map<String, Object>>() {}, result);
                //outcome
                Object data = dataMap.get("_links");
                if (data != null && data instanceof Map) {
                    Map<String, Object> functions = (Map<String, Object>) data;
                    outcome = functions.containsKey(function);
                }
            } catch (IOException e) {}
        }
        return outcome;
    }

}
