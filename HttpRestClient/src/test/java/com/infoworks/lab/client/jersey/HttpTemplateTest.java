package com.infoworks.lab.client.jersey;

import com.infoworks.lab.rest.models.QueryParam;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTemplateTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void loadTest(){
        //case:1
        objectArrayType("/path-a", "path-b");
        //case: 2
        objectArrayType(new QueryParam("offset","0"), new QueryParam("limit", "10"), new QueryParam("status","PENDING"));
        //case: 3
        objectArrayType("/path-a", "path-b", new QueryParam("offset","0"), new QueryParam("limit", "10"), new QueryParam("status",null));
    }

    private <T extends Object> void objectArrayType(T...params){
        if (params instanceof String[]){
            System.out.println("All String " + params.length);
        }else if (params instanceof QueryParam[]){
            System.out.println("All QueryParam " + params.length);
        }else{
            //Means T...params are arbitrary value e.g. "/path-a", "path-b", QueryParam("offset","0"), QueryParam("limit","10") ... etc
            System.out.println("Mixed Types: " + params.length);
            String[] paths = Arrays.stream(params).filter(obj -> obj instanceof String).collect(Collectors.toList()).toArray(new String[0]);
            System.out.println("Paths: " + paths.length);
            QueryParam[] queryParams = Arrays.stream(params).filter(obj -> obj instanceof QueryParam).collect(Collectors.toList()).toArray(new QueryParam[0]);
            System.out.println("Queries: " + queryParams.length);
        }
    }

    @Test
    public void jerseyTest(){
        jersey("/path-a", "path-b", new QueryParam("status",null), new QueryParam("offset","0"), new QueryParam("limit", "10"));
    }

    public <T> void jersey(T...params){
        //Means T...params are arbitrary value e.g. "/path-a", "path-b", QueryParam("offset","0"), QueryParam("limit","10") ... etc
        //First: Separate Paths from mixed array:
        String[] paths = Arrays.stream(params).filter(obj -> obj instanceof String).collect(Collectors.toList()).toArray(new String[0]);
        //Then: Separate QueryParam from mixed array:
        QueryParam[] queryParams = Arrays.stream(params).filter(obj -> obj instanceof QueryParam).collect(Collectors.toList()).toArray(new QueryParam[0]);
        //Finally
        List<String> collector = new ArrayList<>(Arrays.asList(paths));
        collector.add(urlencodedQueryParam(queryParams));
        System.out.println(validatePaths(collector.toArray(new String[0])));
    }

    @Test
    public void okhttpTest(){
        okhttp("/path-a", "path-b", new QueryParam("status",null), new QueryParam("offset","0"), new QueryParam("limit", "10"));
    }


    public <T> void okhttp(T...params){
        //Means T...params are arbitrary value e.g. "/path-a", "path-b", QueryParam("offset","0"), QueryParam("limit","10") ... etc
        //First: Separate Paths from mixed array:
        List<String> paths = new ArrayList<>();
        for (Object query : params) {
            if (query instanceof String)
                paths.add((String) query);
        }
        List<String> collector = new ArrayList<>(paths);
        //Then: Separate QueryParam from mixed array:
        List<QueryParam> queryParams = new ArrayList<>();
        for (Object query : params) {
            if (query instanceof QueryParam)
                queryParams.add((QueryParam) query);
        }
        String queryParam = urlencodedQueryParam(queryParams.toArray(new QueryParam[0]));
        collector.add(queryParam);
        //Finally:
        System.out.println(validatePaths(collector.toArray(new String[0])));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Copied from Main Source-Block:
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String urlencodedQueryParam(QueryParam...params){
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

    private StringBuffer validatePaths(String... params) {
        StringBuffer buffer = new StringBuffer();
        for (String str : params) {
            String trimmed = str.trim();
            if (trimmed.isEmpty()) continue;
            if (trimmed.length() > 2 && trimmed.endsWith("/"))
                trimmed = trimmed.substring(0, trimmed.length() - 1);

            if(trimmed.startsWith("/"))
                buffer.append(trimmed);
            else if(trimmed.startsWith("?"))
                buffer.append(trimmed);
            else
                buffer.append("/" + trimmed);
        }
        return buffer;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}