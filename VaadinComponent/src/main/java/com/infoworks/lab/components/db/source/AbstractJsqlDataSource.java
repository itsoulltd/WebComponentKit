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

    protected Query<E, String> getQuery() {
        if (query == null){
            int pageSize = (Objects.nonNull(getGrid())) ? getGrid().getPageSize() : 10;
            query = new Query(0, pageSize, Collections.emptyList(), null, null);
        }
        return query;
    }

    protected final Query next(Query prev){
        if (prev.getOffset() >= getMaxOffsetQuery().getOffset()) return prev;
        Query next = new Query(prev.getOffset() + prev.getLimit()
                , prev.getLimit()
                , prev.getSortOrders()
                , prev.getInMemorySorting()
                , prev.getFilter().isPresent() ? prev.getFilter().get() : null);
        this.query = next;
        return next;
    }

    protected final Query previous(Query next){
        if (next.getOffset() == 0) return next;
        Query prev = new Query(next.getOffset() - next.getLimit()
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
            LOG.warning(e.getMessage());
        } catch (IllegalAccessException e) {
            LOG.warning(e.getMessage());
        }
        return null;
    }

    private Query maxLimitQuery;

    protected final Query getMaxOffsetQuery() {
        if (maxLimitQuery == null){
            int max = getRowCount();
            maxLimitQuery = new Query(max
                    , getQuery().getLimit()
                    , getQuery().getSortOrders()
                    , getQuery().getInMemorySorting()
                    , getQuery().getFilter().isPresent() ? getQuery().getFilter().get() : null);
        }
        return maxLimitQuery;
    }

    protected final Query<E, String> updateMaxOffsetQuery(int byValue) {
        Query max = new Query(getMaxOffsetQuery().getOffset() + (byValue)
                , getMaxOffsetQuery().getLimit()
                , getMaxOffsetQuery().getSortOrders()
                , getMaxOffsetQuery().getInMemorySorting()
                , getMaxOffsetQuery().getFilter().isPresent() ? getMaxOffsetQuery().getFilter().get() : null);
        this.maxLimitQuery = max;
        updateCellFooter(getGrid());
        return max;
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
        super.prepareGridUI(grid);
        updateCellFooter(grid);
        int length = grid.getColumns().size();
        Grid.Column last = grid.getColumns().get(length - 1);
        HorizontalLayout buttonCell = new HorizontalLayout(new Button(" < ", previousAction)
                , new Button(" > ", nextAction));
        buttonCell.setPadding(false);
        buttonCell.setSpacing(true);
        buttonCell.setSizeFull();
        buttonCell.setAlignItems(FlexComponent.Alignment.CENTER);
        last.setFooter(buttonCell);
        return this;
    }

    protected void updateCellFooter(Grid<E> grid) {
        Grid.Column cell = grid.getColumns().get(0);
        cell.setFooter("Total: " + ((getMaxOffsetQuery() != null)
                ? getMaxOffsetQuery().getOffset()
                : getMemStorage().size()));
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
        if (Objects.isNull(query)) return;
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
            LOG.warning(e.getMessage());
        } catch (IllegalAccessException e) {
            LOG.warning(e.getMessage());
        } catch (InstantiationException e) {
            LOG.warning(e.getMessage());
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
                    int size = getMemStorage().size();
                    if (size < getQuery().getLimit()) {
                        super.save(item);
                    }
                    updateMaxOffsetQuery(1);
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
                    updateMaxOffsetQuery(-1);
                    LOG.info("DELETD: " + id);
                }
            } catch (SQLException e) {
                LOG.warning(e.getMessage());
            }
        }
        return this;
    }

}
