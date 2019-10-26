package com.infoworks.lab.rest.models;

import java.lang.reflect.Field;
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
                        compareWithOrder(order, (C) o1, (C) o2)
                );
                setCollections(new ArrayList(Arrays.asList(items)));
                _isSorted = true;
            }
        }
        return getCollections();
    }

    private int compareWithOrder(SortOrder order, C o1, C o2){
        if (order == SortOrder.ASE)
            return compare(o1, o2);
        else
            return compare(o2, o1);
    }

    protected int compare(C o1, C o2){
        Object obj1 = getSortBy(o1);
        Object obj2 = getSortBy(o2);
        if (obj1 != null && obj2 != null){
            return obj1.toString().compareToIgnoreCase(obj2.toString());
        }else{
            return 0; //So that, list remain as is;
        }
    }

    protected final Object getSortBy(C obj) {
        if (sortByIsEmpty()) return null;
        Field fl = null;
        try {
            fl = obj.getClass().getDeclaredField(_sortBy);
            fl.setAccessible(true);
            return fl.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } finally {
            if (fl != null) fl.setAccessible(false);
        }
        return null;
    }

    protected boolean sortByIsEmpty() {
        return _sortBy == null || _sortBy.isEmpty();
    }
}
