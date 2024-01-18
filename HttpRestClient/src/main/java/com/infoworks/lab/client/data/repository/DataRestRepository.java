package com.infoworks.lab.client.data.repository;

import com.infoworks.lab.client.data.rest.Any;
import com.infoworks.lab.client.data.rest.PaginatedResponse;
import com.infoworks.lab.rest.models.QueryParam;
import com.it.soul.lab.data.base.DataSource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface DataRestRepository<Value extends Any> extends DataSource<Object, Value>, AutoCloseable {
    PaginatedResponse load();
    void load(Consumer<PaginatedResponse> consumer);

    Optional<List<Value>> next();
    void next(Consumer<Optional<List<Value>>> consumer);

    Optional<List<Value>> search(String function, QueryParam... params);
    void search(String function, QueryParam[] params, Consumer<Optional<List<Value>>> consumer);
    boolean isSearchActionExist(String function);

    boolean isLastPage();
    int currentPage();
    int number();
    int totalPages();
    int totalElements();

    @SuppressWarnings("Duplicates")
    default String encodedQueryParams(QueryParam... params) {
        StringBuilder buffer = new StringBuilder("?");
        for (QueryParam query : params) {
            if (query.getValue() == null || query.getValue().isEmpty()) continue;
            try {
                buffer.append(query.getKey()
                        + "="
                        + URLEncoder.encode(query.getValue(), "UTF-8")
                        + "&");
            } catch (UnsupportedEncodingException e) {}
        }
        String value = buffer.toString();
        value = value.substring(0, value.length() - 1);
        return value;
    }
}
