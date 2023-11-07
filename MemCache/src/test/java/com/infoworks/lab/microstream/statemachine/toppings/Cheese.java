package com.infoworks.lab.microstream.statemachine.toppings;

import com.infoworks.lab.microstream.statemachine.pizzas.Pizza;

import java.math.BigDecimal;

public class Cheese extends AbstractToppings {

    public Cheese(Pizza pizza){
        super(pizza);
    }

    public Cheese() {
        this(null);
    }

    @Override
    public String getDescription() {
        return getPizza().getDescription() + " + " + getRecipe().getDescription();
    }

    @Override
    public BigDecimal getCost() {
        return (new BigDecimal(getRecipe().getPrice())).add(getPizza().getCost());
    }
}
