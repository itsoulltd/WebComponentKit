package com.infoworks.lab.microstream.statemachine.pizzas;

import java.math.BigDecimal;

/**
 * This is a concrete component, we will decorate with various decorator
 * e.g. Pepperoni, Cheese, Mushroom, Sausage etc
 */
public class ThickCrustPizza extends AbstractPizza {

    public ThickCrustPizza(){}

    @Override
    public BigDecimal getCost() {
        return new BigDecimal(getRecipe().getPrice());
    }
}
