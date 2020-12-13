package com.infoworks.lab.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.it.soul.lab.sql.query.models.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class SearchQuery extends PagingQuery implements WhereClause {

    public SearchQuery() {
        _queryPredicate = new QueryPredicate();
    }

    private List<QueryProperty> properties = new ArrayList<>();

    public List<QueryProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<QueryProperty> properties) {
        this.properties = properties;
        prepareSortedList(properties);
    }

    protected void addProperty(String key, Logic logic){
        if (getProperties().size() == 0){
            QueryProperty qp = new QueryProperty(key);
            getProperties().add(qp);
        }else{
            QueryProperty previous = getCurrent();
            if (previous != null){
                previous.setLogic(logic);
                previous.setNextKey(key);
            }
            QueryProperty qp = new QueryProperty(key);
            getProperties().add(qp);
        }
        updateSortedList(getCurrent());
    }

    @JsonIgnore
    protected QueryProperty getCurrent(){
        if (getProperties().size() == 0) return null;
        QueryProperty current = getProperties().get(getProperties().size()-1);
        return current;
    }

    @JsonIgnore
    private QueryPredicate _queryPredicate;
    @JsonIgnore
    public Predicate getPredicate(){
        return _queryPredicate;
    }

    public WhereClause add(String key){
        getPredicate().and(key);
        return this;
    }

    public WhereClause remove(String...keys){
        if (keys != null && keys.length > 0){
            //First Take a snapshot of all properties:
            List<QueryProperty> cache = new ArrayList<>(getProperties());
            //Clean Current State:
            removeAll();
            //Loop through all-properties:
            List<String> skipList = Arrays.asList(keys);
            List<QueryProperty> props = cache.stream()
                    .filter(query -> !skipList.contains(query.getKey()))
                    .collect(Collectors.toList());
            //add all except given keys:
            reconstructRelations(props);
            setProperties(props);
            //Done
            return this;
        }else {
            return removeAll();
        }
    }

    private void reconstructRelations(List<QueryProperty> props) {
        QueryProperty carry = null;
        for (QueryProperty query : props) {
            if (carry != null && carry.getNextKey() != null){
                if (!query.getKey().equalsIgnoreCase(carry.getNextKey())){
                    //Means: chain was disconnected due to removal:
                    //Reconstruct the chain:
                    carry.setNextKey(query.getKey());
                }
            }
            carry = query;
        }
        if (carry != null){
            //Set the termination:
            carry.setNextKey(null);
            carry.setLogic(null);
        }
    }

    public WhereClause removeAll(){
        //Remove all:
        synchronized (this.properties) {
            getProperties().clear();
            getSortedList().clear();
            _queryPredicate = new QueryPredicate();
        }
        return this;
    }

    @Override
    public boolean containValidStuff(String value) {
        //TODO: CHECK all sort-descriptor keys for malicious stuff:
        //AVOID: 'OR'/'or'/'AND'/'and' in the value. e.g. SELECT * FROM Users WHERE UserId = 105 OR 1=1;
        //AVOID: ""="" e.g.  SELECT * FROM Users WHERE Name ="" or ""="" AND Pass ="" or ""="";
        //AVOID: Batched SQL Statements. e.g.
                //txtUserId = getRequestString("UserId");
                //txtSQL = "SELECT * FROM Users WHERE UserId = " + txtUserId;
                //User input =>  txtUserId = 105; DROP TABLE Suppliers
                //results in=> SELECT * FROM Users WHERE UserId = 105; DROP TABLE Suppliers;
        //DO: Use SQL Parameters for Protection e.g. SELECT * FROM Customer WHERE userId = ?;
        //And user java prepare-statement commend:
        boolean myStuff = true;
        return super.containValidStuff(value) && myStuff;
    }

    private boolean _isPropertiesSorted;

    @JsonIgnore
    private List<QueryProperty> sortedList;
    private List<QueryProperty> getSortedList(){
        if (sortedList == null){
            sortedList = new ArrayList<>();
        }
        return sortedList;
    }

    private void prepareSortedList(List<QueryProperty> properties){
        if (properties == null) return;
        if (properties.size() > 0 && getSortedList().size() <= 0){
            if (_isPropertiesSorted == false){
                getSortedList().addAll(properties);
                getSortedList().sort((QueryProperty o1, QueryProperty o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()));
                _isPropertiesSorted = true;
            }
        }
    }

    private void updateSortedList(QueryProperty query){
        if (query == null) return;
        //First find the expected index to insert:
        //Then insert new Item into that index:
        //This guarantees that the return value will be >= 0 if and only if the key is found.
        //And this must be the index of the search key, if it is contained in the list;
        //Otherwise, -(returnIndex) = (-(insertionPoint) - 1). The insertion point is defined as the point at which the key would be inserted into the list:
        //So, insertionPoint = returnIndex - 1;
        //New Implementation:
        QueryProperty goingToInserted = query;
        List<QueryProperty> sorted = getSortedList();
        int index = (sorted.isEmpty()) ? 0 : Collections.binarySearch(sorted, goingToInserted, (o1, o2) -> {
            int res = o1.getKey().compareToIgnoreCase(o2.getKey());
            return res;
        });
        if (index < 0) {
            int insertIndex = ((index * -1) - 1);
            if (insertIndex >= 0 && insertIndex < sorted.size()) {
                index = insertIndex;
            }else{
                index = sorted.size();
            }
        }
        //So just insert at this index, because we support duplicate insert.
        if (index < sorted.size()) {
            sorted.add(index, goingToInserted);
        } else {
            sorted.add(goingToInserted);
        }
    }

    @JsonIgnore
    public Object get(String key){
        return get(key, null);
    }

    @JsonIgnore
    public <T extends Object> T get(String key, Class<T> classType){
        //Search into properties
        int index = Collections.binarySearch(getSortedList()
                , new QueryProperty(key), (o1, o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()));
        //
        if (index < 0) return null;
        QueryProperty props = getSortedList().get(index);
        String value = props.getValue();
        if (value == null) return null;
        //
        DataType type = props.getType();
        Object result = value;
        switch (type){
            case INT:
                result = Integer.valueOf(value);
                break;
            case LONG:
                result = Long.valueOf(value);
                break;
            case FLOAT:
                result = Float.valueOf(value);
                break;
            case BOOL:
                result = Boolean.valueOf(value);
                break;
            case DOUBLE:
                result = Double.valueOf(value);
                break;
            case BIG_DECIMAL:
                result = new BigDecimal(value);
                break;
            case UUID:
                result = UUID.fromString(value);
                break;
            case SQLDATE:
            case SQLTIMESTAMP:
                SimpleDateFormat format = new SimpleDateFormat(Property.SQL_DATETIME_FORMAT);
                try {
                    result = format.parse(value);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case JSON:
                try {
                    result = Message.unmarshal(classType, value);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                result = value;
        }
        return (T) result;
    }

    protected void updateCurrentProperty(Object o, Operator opt) {
        if (getCurrent() == null) return;
        QueryProperty current = getCurrent();
        if (validate(o)) {
            if (o instanceof Object[]){
                current.setType(DataType.getDataType(o));
                List<String> a = Arrays.stream(((Object[]) o))
                        .map(obj -> "'"+obj.toString()+"'")
                        .collect(Collectors.toList());
                current.setValue(String.join(",", a.toArray(new String[0])));
            }else if(o instanceof Message) {
                String strValue = o.toString();
                current.setType(DataType.getDataType(strValue));
                current.setValue(strValue);
            }else {
                current.setType(DataType.getDataType(o));
                current.setValue(String.valueOf(o));
            }
        }
        current.setOperator(opt);
    }

    @Override
    public Predicate isEqualTo(Object o) {
        updateCurrentProperty(o, Operator.EQUAL);
        return getPredicate();
    }

    @Override
    public Predicate notEqualTo(Object o) {
        updateCurrentProperty(o, Operator.NOTEQUAL);
        return getPredicate();
    }

    @Override
    public Predicate isGreaterThen(Object o) {
        updateCurrentProperty(o, Operator.GREATER_THAN);
        return getPredicate();
    }

    @Override
    public Predicate isGreaterThenOrEqual(Object o) {
        updateCurrentProperty(o, Operator.GREATER_THAN_OR_EQUAL);
        return getPredicate();
    }

    @Override
    public Predicate isLessThen(Object o) {
        updateCurrentProperty(o, Operator.LESS_THAN);
        return getPredicate();
    }

    @Override
    public Predicate isLessThenOrEqual(Object o) {
        updateCurrentProperty(o, Operator.LESS_THAN_OR_EQUAL);
        return getPredicate();
    }

    @Override
    public Predicate isIn(Object...o) {
        updateCurrentProperty(o, Operator.IN);
        return getPredicate();
    }

    @Override
    public Predicate notIn(Object...o) {
        updateCurrentProperty(o, Operator.NOT_IN);
        return getPredicate();
    }

    @Override
    public Predicate isLike(Object o) {
        updateCurrentProperty(o, Operator.LIKE);
        return getPredicate();
    }

    @Override
    public Predicate notLike(Object o) {
        updateCurrentProperty(o, Operator.NOT_LIKE);
        return getPredicate();
    }

    @Override
    public Predicate isNull() {
        updateCurrentProperty(null, Operator.IS_NULL);
        return getPredicate();
    }

    @Override
    public Predicate notNull() {
        updateCurrentProperty(null, Operator.NOT_NULL);
        return getPredicate();
    }

    public static class QueryProperty {
        private String key;
        private String value;
        private Operator operator;
        private DataType type;
        private String nextKey;
        private Logic logic;

        public QueryProperty(String key) {
            this.key = key;
        }

        public QueryProperty() {}

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public DataType getType() {
            return type;
        }

        public void setType(DataType type) {
            this.type = type;
        }

        public String getNextKey() {
            return nextKey;
        }

        public void setNextKey(String nextKey) {
            this.nextKey = nextKey;
        }

        public Logic getLogic() {
            return logic;
        }

        public void setLogic(Logic logic) {
            this.logic = logic;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Operator getOperator() {
            return operator;
        }

        public void setOperator(Operator operator) {
            this.operator = operator;
        }
    }

    private class QueryPredicate implements Predicate{

        @Override
        public WhereClause and(String s) {
            SearchQuery.this.addProperty(s, Logic.AND);
            return SearchQuery.this;
        }

        @Override
        public WhereClause or(String s) {
            SearchQuery.this.addProperty(s, Logic.OR);
            return SearchQuery.this;
        }

        private ExpressionInterpreter expression;

        @Override
        public Predicate not() {
            return this;
        }

        @Override
        public Predicate and(ExpressionInterpreter expressionInterpreter) {
            return this;
        }

        @Override
        public Predicate or(ExpressionInterpreter expressionInterpreter) {
            return this;
        }

        private Predicate createExpression(QueryProperty property, Logic logic) {
            ExpressionInterpreter exp = new Expression(new Property(property.getKey(), property.getValue()), property.getOperator());
            return create(exp, logic);
        }

        private Predicate create(ExpressionInterpreter exp, Logic logic){
            if(expression == null) {
                expression = exp;
            }
            if(logic != null) {
                if(logic == Logic.AND) { createAnd(exp);}
                else {createOr(exp);}
            }
            return this;
        }

        private void createAnd(ExpressionInterpreter exp) {
            expression = new AndExpression(expression, exp);
        }

        private void createOr(ExpressionInterpreter exp) {
            expression = new OrExpression(expression, exp);
        }

        @Override
        public String interpret() {
            if (expression == null){
                Logic logic = null;
                for (QueryProperty property : getProperties()) {
                    if (!validate(property.getValue())) continue;
                    createExpression(property, logic);
                    logic = property.getLogic();
                }
            }
            return (expression != null) ? expression.interpret() : "";
        }

        @Override
        public Expression[] resolveExpressions() {
            if (expression == null){
                Logic logic = null;
                for (QueryProperty property : getProperties()) {
                    if (!validate(property.getValue())) continue;
                    createExpression(property, logic);
                    logic = property.getLogic();
                }
            }
            return (expression != null) ? expression.resolveExpressions() : new Expression[0];
        }
    }

}
