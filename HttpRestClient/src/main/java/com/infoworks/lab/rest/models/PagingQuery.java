package com.infoworks.lab.rest.models;

import com.infoworks.lab.rest.models.events.Event;
import com.infoworks.lab.rest.models.pagination.Pagination;
import com.infoworks.lab.rest.models.pagination.SortDescriptor;

import java.util.Arrays;
import java.util.List;

public class PagingQuery<Q extends Pagination> extends Event implements Pagination<Q> {

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

    @Override
    public Q next() {
        Q query = jumpTo(++page);
        return query;
    }

    @Override
    public Q jumpTo(Integer page) {
        if (page < 0) return null;
        PagingQuery query = null;
        try {
            query = cloneMe(getClass());
            query.setPage(page);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return (Q) query;
    }

    @Override
    public Q previous() {
        if (page == 0) return null;
        Q query = jumpTo(--page);
        return query;
    }

    protected <T extends PagingQuery> T cloneMe(Class<T> type)
            throws IllegalAccessException, InstantiationException {
        T myClone = type.newInstance();
        myClone.unmarshallingFromMap(this.marshallingToMap(true), true);
        return myClone;
    }

}
