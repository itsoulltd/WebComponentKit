package com.infoworks.lab.statemachine.toppings;

import com.infoworks.lab.statemachine.pizzas.Pizza;

import java.math.BigDecimal;

public class Sausage extends AbstractToppings {

    public Sausage(Pizza pizza){
        super(pizza);
    }

    public Sausage() {this(null);}

    @Override
    public String getDescription() {
        return getPizza().getDescription() + " + " + getRecipe().getDescription();
    }

    @Override
    public BigDecimal getCost() {
        return (new BigDecimal(getRecipe().getPrice())).add(getPizza().getCost());
    }
}
