package com.infoworks.lab.statemachine.pizzas;

import com.infoworks.lab.statemachine.PizzaRecipe;

public abstract class AbstractPizza implements Pizza{

    public String getDescription(){
        return recipe.getDescription();
    }

    protected PizzaRecipe recipe;
    public PizzaRecipe getRecipe() {
        return recipe;
    }
    public void setRecipe(PizzaRecipe recipe) { this.recipe = recipe; }

}
