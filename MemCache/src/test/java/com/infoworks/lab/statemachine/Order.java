package com.infoworks.lab.statemachine;

import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.entity.PrimaryKey;
import com.it.soul.lab.sql.entity.TableName;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@javax.persistence.Entity(name = "`ORDER`")
@TableName(value = "`ORDER`", acceptAll = false)
public class Order extends Entity {

    @Id @PrimaryKey(name="`ORDER_ID`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="`ORDER_ID`", length = 100)
    private String orderId;

    @Column(name = "`CUSTOMER_EMAIL`")
    private String customerEmail;

    @Column(name = "`PRICE`")
    private String price;

    @Column(name = "`PIZZA_NAME`")
    private String pizzaClassName;

    @Column(name = "`STATE`")
    private String stateClassName;

    @Column(name = "`DESCRIPTION`")
    private String description;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(name = "`ORDER_TOPPINGS`")
    @Column(name = "`TOPPINGS`")
    private List<String> toppingClassNames = new ArrayList<>();

    public Order() {
        this(UUID.randomUUID().toString());
    }

    public Order(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPizzaClassName() {
        return pizzaClassName;
    }

    public void setPizzaClassName(String pizzaClassName) {
        this.pizzaClassName = pizzaClassName;
    }

    public String getStateClassName() {
        return stateClassName;
    }

    public void setStateClassName(String stateClassName) {
        this.stateClassName = stateClassName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getToppingClassNames() {
        return toppingClassNames;
    }

    public void setToppingClassNames(List<String> toppingClassNames) {
        this.toppingClassNames = toppingClassNames;
    }
}
