package com.infoworks.lab.statemachine.pizzas;

import com.infoworks.lab.statemachine.PizzaRecipe;

import java.math.BigDecimal;

/**
 * This is the component we love to decorate on the fly.
 */
public interface Pizza {
    PizzaRecipe getRecipe();
    void setRecipe(PizzaRecipe recipe);
    String getDescription();
    BigDecimal getCost();
    default float convertCost() {return Float.valueOf(getCost().toPlainString());}
    default String printedCost() {return String.format("%.2f", convertCost());}
}
