package com.infoworks.lab.statemachine.toppings;

import com.infoworks.lab.statemachine.pizzas.Pizza;

/**
 * This is our interface for decorating our pizza toppings.
 */
public interface Toppings extends Pizza {
    void setPizza(Pizza pizza);
}
