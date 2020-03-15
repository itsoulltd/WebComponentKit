package com.infoworks.lab.rest.models.pagination;

import java.util.Arrays;
import java.util.List;

public interface Pagination<P extends Pagination> {

    P next();
    P jumpTo(Integer page);
    P previous();

    Integer getPage();
    void setPage(Integer page);
    Integer getSize();
    void setSize(Integer size);
    List<SortDescriptor> getDescriptors();
    void setDescriptors(List descriptors);

    static <T extends Pagination> T createQuery(Class<T> type, int size, SortOrder order, String...keys) {
        Pagination query = null;
        try {
            query = type.newInstance();
            query.setPage(0);
            query.setSize(size);
            SortDescriptor des = new SortDescriptor(order);
            des.setKeys(Arrays.asList(keys));
            query.setDescriptors(Arrays.asList(des));
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) query;
    }
}
