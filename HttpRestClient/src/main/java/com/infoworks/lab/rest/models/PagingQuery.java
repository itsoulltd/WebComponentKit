package com.infoworks.lab.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.rest.models.events.Event;
import com.infoworks.lab.rest.models.pagination.Queryable;
import com.infoworks.lab.rest.models.pagination.SortDescriptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PagingQuery<E extends Event> extends Message<E> implements Queryable<PagingQuery> {

    private Integer page = 0;
    private Integer size = 0;
    private List<SortDescriptor> descriptors;

    public PagingQuery() {
        descriptors = Arrays.asList(new SortDescriptor());
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<SortDescriptor> getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(List<SortDescriptor> descriptors) {
        this.descriptors = descriptors;
    }

    @JsonIgnore
    public boolean isQueryable(){
        return isValidJson(getPayload());
    }

    @Override
    public void updatePayload() {
        try {
            setPayload(marshalMessagePayload(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PagingQuery next() {
        PagingQuery query = jumpTo(++page);
        return query;
    }

    @Override
    public PagingQuery jumpTo(Integer page) {
        if (page < 0) return null;
        PagingQuery query = null;
        try {
            query = cloneMe(getClass());
            query.page = page;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return query;
    }

    @Override
    public PagingQuery previous() {
        if (page == 0) return null;
        PagingQuery query = jumpTo(--page);
        return query;
    }

    protected <T extends PagingQuery> T cloneMe(Class<T> type) throws IOException {
        ObjectMapper mapper = getJsonSerializer();
        String jsonString = mapper.writeValueAsString(this);
        T query = mapper.readValue(jsonString, type);
        return query;
    }

}
