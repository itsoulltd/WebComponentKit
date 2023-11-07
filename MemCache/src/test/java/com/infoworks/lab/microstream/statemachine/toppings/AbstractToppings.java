package com.infoworks.lab.microstream.statemachine.toppings;

import com.infoworks.lab.microstream.statemachine.PizzaRecipe;
import com.infoworks.lab.microstream.statemachine.pizzas.Pizza;

public abstract class AbstractToppings implements Toppings {

    protected Pizza pizza;
    public Pizza getPizza() {
        return pizza;
    }
    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }

    protected PizzaRecipe recipe;
    public PizzaRecipe getRecipe() {
        return recipe;
    }
    public void setRecipe(PizzaRecipe recipe) { this.recipe = recipe; }

    public AbstractToppings(Pizza pizza) {
        this.pizza = pizza;
    }
}
