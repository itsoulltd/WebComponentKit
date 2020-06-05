package com.infoworks.lab.client.jersey;

import com.infoworks.lab.rest.models.QueryParam;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
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

    }

    @Test
    public void httookTests(){

    }
}