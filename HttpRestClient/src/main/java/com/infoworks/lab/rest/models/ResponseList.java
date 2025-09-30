package com.infoworks.lab.rest.models;

import com.infoworks.lab.rest.models.pagination.SortOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResponseList<C extends Response> extends Response{

    private boolean _isSorted = false;
    private List<C> collections;
    private String _sortBy;

    public ResponseList() {/**/}

    public ResponseList(List<C> collections) {
        this.collections = collections;
    }

    public List<C> getCollections() {
        return collections;
    }

    public void setCollections(List<C> collections) {
        this.collections = collections;
    }

    public final List<C> sort(SortOrder order, String key){
        if (_isSorted == false) {
            _sortBy = key;
            synchronized (this){
                Response[] items = getCollections().toArray(new Response[0]);
                Arrays.sort(items, (o1, o2) ->
                        compareWithOrder((C) o1, (C) o2, key, order)
                );
                setCollections(new ArrayList(Arrays.asList(items)));
                _isSorted = true;
            }
        }
        return getCollections();
    }
}
