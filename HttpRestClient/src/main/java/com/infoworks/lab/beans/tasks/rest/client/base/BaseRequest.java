package com.infoworks.lab.beans.tasks.rest.client.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.beans.tasks.nuts.ExecutableTask;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.rest.models.ResponseList;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public abstract class BaseRequest<In extends Message, Out extends Response> extends ExecutableTask<In, Out> {

    @SuppressWarnings("Duplicates")
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

    protected StringBuilder validatePaths(String... params) {
        StringBuilder buffer = new StringBuilder();
        for(String str : Arrays.asList(params)) {
            String trimmed = str.trim();
            if (trimmed.isEmpty()) continue;
            if (trimmed.length() > 2 && trimmed.endsWith("/"))
                trimmed = trimmed.substring(0, trimmed.length() - 1);

            if(trimmed.startsWith("/"))
                buffer.append(trimmed);
            else
                buffer.append("/" + trimmed);
        }
        return buffer;
    }

    protected <T extends Response> List<T> inflateJson(String json, Class<T> type) throws IOException {
        ObjectMapper mapper = Message.getJsonSerializer();
        if (json != null && !json.isEmpty()){
            if (json.startsWith("{")){
                return Arrays.asList(mapper.readValue(json, type));
            }else if(json.startsWith("[")){
                List result = new ArrayList();
                List items = mapper.readValue(json, ArrayList.class);
                Iterator itr = items.iterator();
                while (itr.hasNext()){
                    Object dts = itr.next();
                    if (dts instanceof Map){
                        T instance = mapper.convertValue(dts, type);
                        result.add(instance);
                    }
                }
                ResponseList rsList = new ResponseList(result);
                return (List<T>) Arrays.asList(rsList);
            }
        }
        return (List<T>) Arrays.asList(new Response().setMessage(json));
    }

}
