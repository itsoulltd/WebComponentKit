package com.infoworks.lab.rest.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.rest.models.pagination.Pagination;
import com.infoworks.lab.rest.models.pagination.SortOrder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class PagingQueryTest {
    Message<PagingQuery> consume;
    ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = Message.getJsonSerializer();
        consume = new Message<>(PagingQuery.class);
    }

    @After
    public void tearDown() throws Exception {
        consume = null;
    }

    @Test
    public void testConversion() throws JsonProcessingException {
        PagingQuery query = new PagingQuery();
        String json = mapper.writeValueAsString(query);
        Assert.assertTrue(json != null);
    }

    @Test
    public void testConstruction() throws IOException {
        String json = "{\"offset\":0,\"size\":10,\"previousOffset\":0,\"descriptors\":[{\"order\":\"ASC\",\"keys\":[\"name\"]},{\"order\":\"DESC\",\"keys\":[\"age\",\"salary\"]}]}";
        //String json = "{\"offset\":0,\"size\":0,\"previousOffset\":0,\"descriptors\":[{\"order\":\"ASC\",\"keys\":null}]}";
        PagingQuery query = mapper.readValue(json, PagingQuery.class);
        Assert.assertTrue(query != null);
    }

    @Test
    public void testNextConversion() throws JsonProcessingException {
        PagingQuery page = Pagination.createQuery(PagingQuery.class, 10, SortOrder.DESC, "name","age","salary");

        consume.setEvent(page);
        PagingQuery query = consume.getEvent();

        System.out.println("marshal Result 1:"+ query.toString());

        consume.setEvent(query.next());
        System.out.println("marshal Result 2:"+ query.toString());

        consume.setEvent(query.next());
        System.out.println("marshal Result 3:"+ query.toString());

    }

    @Test
    public void testPreviousConversion() throws JsonProcessingException {
        PagingQuery page = Pagination.createQuery(PagingQuery.class, 10, SortOrder.DESC, "name","age","salary");

        consume.setEvent(page);
        PagingQuery query = consume.getEvent();

        query = query.jumpTo(3);
        consume.setEvent(query);
        System.out.println("marshal Result 1:"+ query.toString());

        query = query.previous();
        consume.setEvent(query);
        System.out.println("marshal Result 2:"+ query.toString());

        query = query.previous();
        consume.setEvent(query);
        System.out.println("marshal Result 3:"+ query.toString());

        query = query.previous();
        consume.setEvent(query);
        System.out.println("marshal Result 4:"+ query.toString());

    }

    @Test
    public void testNextConstruction() throws IOException {
        String json = "{\"offset\":0,\"size\":10,\"previousOffset\":0,\"descriptors\":[{\"order\":\"ASC\",\"keys\":[\"name\"]},{\"order\":\"DESC\",\"keys\":[\"age\",\"salary\"]}]}";

        PagingQuery query = mapper.readValue(json, PagingQuery.class);
        System.out.println("unmarshal Result 1:"+ query.toString());

        PagingQuery query2 = query.next();
        System.out.println("unmarshal Result 2:"+ query2.toString());

        PagingQuery query3 = query2.next();
        System.out.println("unmarshal Result 3:"+ query3.toString());

    }
}