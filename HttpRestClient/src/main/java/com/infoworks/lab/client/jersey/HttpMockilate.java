package com.infoworks.lab.client.jersey;

import com.infoworks.lab.exceptions.HttpInvocationException;
import com.infoworks.lab.mock.Mockitor;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.Response;
import com.it.soul.lab.sql.entity.EntityInterface;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class HttpMockilate<P extends Response, C extends EntityInterface> extends HttpTemplate<P, C> {

    private Map<String, Mockitor> mockitorMap = new ConcurrentHashMap<>();

    public void registerPathToMockitor(String path, Mockitor<P> mockitor){
        try {
            if ((path != null)) {
                mockitorMap.put(resourcePath(validatePaths(path).toString()), mockitor);
            }else {
                mockitorMap.put(resourcePath(), mockitor);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    protected long getRandomSleepTime(){
        Random random = new Random();
        long sleepTime = 100 * random.nextInt(12) + 1;
        return sleepTime;
    }

    @Override
    public P get(C c, QueryParam... params) throws HttpInvocationException {
        try {
            Thread.sleep(getRandomSleepTime());
            String path = resourcePath();
            Mockitor<P> mockitor = mockitorMap.get(path);
            P item = mockitor.accept();
            return item;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void get(C c, List<QueryParam> list, Consumer<P> consumer) {
        addConsumer(consumer);
        submit(() -> {
            try {
                P res = get(c, list.toArray(new QueryParam[0]));
                notify(res);
            } catch (HttpInvocationException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public P post(C c, String... strings) throws HttpInvocationException {
        try {
            Thread.sleep(getRandomSleepTime());
            String path = resourcePath(strings);
            Mockitor<P> mockitor = mockitorMap.get(path);
            P item = mockitor.accept();
            return item;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void post(C c, List<String> list, Consumer<P> consumer) {
        addConsumer(consumer);
        submit(() -> {
            try {
                P res = post(c, list.toArray(new String[0]));
                notify(res);
            } catch (HttpInvocationException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public P put(C c, String... strings) throws HttpInvocationException {
        return post(c, strings);
    }

    @Override
    public void put(C c, List<String> list, Consumer<P> consumer) {
        post(c, list, consumer);
    }

    @Override
    public boolean delete(C c, QueryParam... queryParams) throws HttpInvocationException {
        return get(c, queryParams) != null;
    }

    @Override
    public void delete(C c, List<QueryParam> list, Consumer<P> consumer) {
        get(c, list, consumer);
    }

    @Override
    public <T> URI getUri(T... ts) {
        return null;
    }

    @Override
    public void configure(Object... objects) throws InstantiationException {
        super.configure(objects);
    }
}
