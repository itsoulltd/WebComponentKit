package com.infoworks.lab.components.db.source;

import com.infoworks.lab.components.crud.components.datasource.DefaultDataSource;
import com.infoworks.lab.components.crud.components.datasource.GridDataSource;
import com.it.soul.lab.sql.QueryExecutor;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.models.Predicate;
import com.it.soul.lab.sql.query.models.Where;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.Query;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public abstract class AbstractJsqlDataSource<E extends Entity> extends DefaultDataSource<E> implements JsqlDataSource<E> {

    private Query query;

    @Override
    public Query<E, String> getQuery() {
        if (query == null){
            query = new Query(0, 10, Collections.emptyList(), null, null);
        }
        return query;
    }

    protected final Query next(Query prev){
        if (prev.getOffset() >= getMaxOffsetQuery().getOffset()) return prev;
        Query next = new Query(prev.getOffset() + prev.getLimit() +1
                , prev.getLimit()
                , prev.getSortOrders()
                , prev.getInMemorySorting()
                , prev.getFilter().isPresent() ? prev.getFilter().get() : null);
        this.query = next;
        return next;
    }

    protected final Query previous(Query next){
        if (next.getOffset() == 0) return next;
        Query prev = new Query(next.getOffset() - next.getLimit() -1
                , next.getLimit()
                , next.getSortOrders()
                , next.getInMemorySorting()
                , next.getFilter().isPresent() ? next.getFilter().get() : null);
        this.query = prev;
        return prev;
    }

    protected final Query copyWith(Query<E, String> from, String filter){
        Query now = new Query(from.getOffset()
                , from.getLimit()
                , from.getSortOrders()
                , from.getInMemorySorting()
                , filter);
        this.query = now;
        return now;
    }

    @Override
    public GridDataSource addSearchFilter(String filter) {
        if (filter.length() <= 3) {
            if (filter.length() <= 0){
                SQLSelectQuery query = getSearchQuery(copyWith(getQuery(), null));
                executeQuery(query);
                return super.addSearchFilter("");
            }else {
                return super.addSearchFilter(filter);
            }
        }
        Query query = copyWith(getQuery(), filter);
        SQLSelectQuery sqlquery = getSearchQuery(query);
        executeQuery(sqlquery);
        reloadGrid();
        return this;
    }

    protected final Predicate createSearchPredicate(Query<E, String> query){
        if (!query.getFilter().isPresent()) return null;
        try {
            E item = getBeanType().newInstance();
            Iterator<String> itr = item.marshallingToMap(false).keySet().iterator();
            Predicate predicate = null;
            while (itr.hasNext()){
                String key = itr.next();
                if (predicate == null){
                    predicate = new Where(key).isEqualTo(query.getFilter().get());
                }else {
                    predicate.or(key).isEqualTo(query.getFilter().get());
                }
            }
            return predicate;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private QueryExecutor executor;
    @Override
    public QueryExecutor getExecutor() {
        return executor;
    }

    @Override
    public JsqlDataSource setExecutor(QueryExecutor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public GridDataSource prepareGridUI(Grid<E> grid) {
        Grid.Column cell = grid.getColumns().get(0);
        cell.setFooter("Total: " + getMemStorage().size());
        int length = grid.getColumns().size();
        Grid.Column last = grid.getColumns().get(length - 1);
        HorizontalLayout buttonCell = new HorizontalLayout(new Button(" < ", previousAction)
                , new Button(" > ", nextAction));
        buttonCell.setPadding(false);
        buttonCell.setSpacing(true);
        buttonCell.setSizeFull();
        buttonCell.setAlignItems(FlexComponent.Alignment.CENTER);
        last.setFooter(buttonCell);
        return super.prepareGridUI(grid);
    }

    private ComponentEventListener<ClickEvent<Button>> previousAction = (event) -> {
        Query previous = previous(getQuery());
        SQLSelectQuery query = getSelectQuery(previous);
        executeQuery(query);
        reloadGrid();
    };

    private ComponentEventListener<ClickEvent<Button>> nextAction = (event) -> {
        Query next = next(getQuery());
        SQLSelectQuery query = getSelectQuery(next);
        executeQuery(query);
        reloadGrid();
    };

    @Override
    public void reloadGrid() {
        //Fetch data from persistence data Source and load into storage:
        if (getMemStorage().size() == 0){
            SQLSelectQuery query = getSelectQuery(getQuery());
            executeQuery(query);
            //TODO: has UI impact:
            getMaxOffsetQuery();
        }
        super.reloadGrid();
    }

    protected final void executeQuery(SQLSelectQuery query) {
        try {
            List<E> items = getExecutor().executeSelect(query
                    , getBeanType()
                    , Entity.mapColumnsToProperties(getBeanType()));
            if (items.size() > 0){
                getMemStorage().clear();
                items.stream()
                        .forEach(item -> super.save(item));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GridDataSource save(E item) {

        if (Objects.isNull(item)) return this;

        Object id = getMemStorage().get(item.hashCode());
        if (Objects.nonNull(id)){
            try {
                if (item.update(getExecutor())) {
                    super.save(item);
                    LOG.info("UPDATED: " + id);
                }
            } catch (SQLException e) {
                LOG.warning(e.getMessage());
            }
        }else {
            try {
                if (item.insert(getExecutor())) {
                    //TODO: Find out is this object visible right now or not?
                    //super.save(item);
                    LOG.info("INSERTED: " + item.toString());
                }
            } catch (SQLException e) {
                LOG.warning(e.getMessage());
            }
        }
        return this;
    }

    @Override
    public GridDataSource delete(E item) {

        if (Objects.isNull(item)) return this;

        Object id = getMemStorage().get(item.hashCode());
        if (Objects.nonNull(id)){
            try {
                if (item.delete(getExecutor())) {
                    super.delete(item);
                    LOG.info("DELETD: " + id);
                }
            } catch (SQLException e) {
                LOG.warning(e.getMessage());
            }
        }
        return this;
    }

}
