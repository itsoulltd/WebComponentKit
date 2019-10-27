package com.infoworks.lab.components.rest;

import com.infoworks.lab.client.jersey.HttpTemplate;
import com.infoworks.lab.jsql.DataSourceKey;
import com.infoworks.lab.jsql.DataSourceKey.Keys;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.models.QueryParam;
import com.infoworks.lab.rest.models.ResponseList;
import com.infoworks.lab.rest.template.Interactor;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.*;
import com.it.soul.lab.sql.query.builder.AbstractQueryBuilder;
import com.it.soul.lab.sql.query.models.Row;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestExecutor extends AbstractRestExecutor {

    private DataSourceKey sourceKey;

    public RestExecutor(DataSourceKey sourceKey) {
        this.sourceKey = sourceKey;
    }

    public DataSourceKey getSourceKey() {
        return sourceKey;
    }

    protected String parseUriString(DataSourceKey sourceKey){
        //
        String schema = getSourceKey().get(Keys.SCHEMA);
        if (schema.startsWith(Keys.SCHEMA.defaultValue())){
            schema = "http://";
        }
        String pathName = getSourceKey().get(Keys.NAME);
        if (pathName == null || pathName.isEmpty()){
            pathName = "";
        }
        while (pathName.startsWith("/")){
            pathName = pathName.substring(1);
        }
        //
        return String.format("%s%s:%s/%s"
                , schema
                , getSourceKey().get(Keys.HOST)
                , getSourceKey().get(Keys.PORT)
                , pathName
        );
    }

    protected URI parseURI(DataSourceKey sourceKey){
        URI uri = URI.create(parseUriString(sourceKey));
        return uri;
    }

    @Override
    public AbstractQueryBuilder createQueryBuilder(QueryType queryType) {
        return new SQLQuery.Builder(queryType);
    }

    private QueryParam[] getQueryParams(SQLQuery query){
        Row row = query.getWhereProperties();
        if (row != null){
            List<QueryParam> queries = row.getCloneProperties().stream()
                    .filter(property -> property.getValue() != null)
                    .flatMap(property -> {
                        QueryParam param = new QueryParam(property.getKey(), property.getValue().toString());
                        return Stream.of(param);
                    }).collect(Collectors.toList());
            if (query instanceof SQLSelectQuery){
                SQLSelectQuery sel = (SQLSelectQuery) query;
                //How To Get Limit & Offset:
                List<QueryParam> limits = new ArrayList<>(queries);
                limits.add(new QueryParam("limit", sel.getLimit().toString()));
                limits.add(new QueryParam("offset", sel.getOffset().toString()));
                queries = limits;
            }
            return queries.toArray(new QueryParam[0]);
        }
        return new QueryParam[]{};
    }

    @Override
    public Integer getScalarValue(SQLScalarQuery query) throws SQLException {
        //Calls Come Here
        URI uri = URI.create(parseUriString(getSourceKey()) + "/rowCount");
        try (HttpTemplate<ItemCount, Entity> template = Interactor.create(HttpTemplate.class, uri, ItemCount.class)){
            ItemCount count = template.get(null, getQueryParams(query));
            return count.getCount().intValue();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    public List executeSelect(SQLSelectQuery query, Class aClass, Map map) throws SQLException, IllegalArgumentException
            , IllegalAccessException
            , InstantiationException {
        //Calls Come Here
        URI uri = parseURI(getSourceKey());
        try (HttpTemplate<ResponseList, Entity> template = Interactor.create(HttpTemplate.class, uri, ItemCount.class)){
            ResponseList list = template.get(null, getQueryParams(query));
            return list.getCollections();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    public Integer executeInsert(boolean b, SQLInsertQuery query) throws SQLException, IllegalArgumentException {
        //Calls Come Here
        URI uri = parseURI(getSourceKey());
        try (HttpTemplate<ItemCount, Entity> template = Interactor.create(HttpTemplate.class, uri, ItemCount.class)){
            Payload payload = new Payload(query.getRow().keyObjectMap());
            ItemCount inserted = template.post(payload);
            return inserted.getCount().intValue();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    public Integer executeUpdate(SQLUpdateQuery query) throws SQLException {
        //Calls Come Here
        URI uri = parseURI(getSourceKey());
        try (HttpTemplate<ItemCount, Entity> template = Interactor.create(HttpTemplate.class, uri, ItemCount.class)){
            Payload payload = new Payload(query.getRow().keyObjectMap());
            ItemCount inserted = template.put(payload);
            return inserted.getCount().intValue();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    public Integer executeDelete(SQLDeleteQuery query) throws SQLException {
        //Calls Come Here
        URI uri = parseURI(getSourceKey());
        try (HttpTemplate template = Interactor.create(HttpTemplate.class, uri)){
            boolean deleted = template.delete(null, getQueryParams(query));
            return deleted ? 1 : 0;
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {
        //
    }

}
