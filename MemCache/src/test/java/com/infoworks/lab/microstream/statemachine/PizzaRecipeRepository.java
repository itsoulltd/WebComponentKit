package com.infoworks.lab.microstream.statemachine;

import com.infoworks.lab.microstream.statemachine.pizzas.CheeseCrustPizza;
import com.infoworks.lab.microstream.statemachine.pizzas.ThickCrustPizza;
import com.infoworks.lab.microstream.statemachine.pizzas.ThinCrustPizza;
import com.infoworks.lab.microstream.statemachine.toppings.Cheese;
import com.infoworks.lab.microstream.statemachine.toppings.Mushroom;
import com.infoworks.lab.microstream.statemachine.toppings.Pepperoni;
import com.infoworks.lab.microstream.statemachine.toppings.Sausage;
import com.it.soul.lab.data.simple.SimpleDataSource;

import java.util.Optional;

public class PizzaRecipeRepository {

    private final SimpleDataSource<String, PizzaRecipe> storage;

    public PizzaRecipeRepository() {
        storage = new SimpleDataSource<>();
        //Adding Recipes:
        storage.put(ThinCrustPizza.class.getName(), new PizzaRecipe("1", "12.00", ThinCrustPizza.class.getName(), "Thin Crust Pizza"));
        storage.put(ThickCrustPizza.class.getName(), new PizzaRecipe("2", "15.00", ThickCrustPizza.class.getName(), "Thick Crust Pizza"));
        storage.put(CheeseCrustPizza.class.getName(), new PizzaRecipe("3", "20.20", CheeseCrustPizza.class.getName(), "Cheese Crust Pizza"));
        //
        storage.put(Cheese.class.getName(), new PizzaRecipe("4", "1.00", Cheese.class.getName(), "Cheese"));
        storage.put(Mushroom.class.getName(), new PizzaRecipe("5", "2.00", Mushroom.class.getName(), "Mushroom"));
        storage.put(Pepperoni.class.getName(), new PizzaRecipe("6", "1.50", Pepperoni.class.getName(), "Pepperoni"));
        storage.put(Sausage.class.getName(), new PizzaRecipe("7", "2.70", Sausage.class.getName(), "Sausage"));
        //
    }

    public void clear() {
        storage.clear();
    }

    public Optional<PizzaRecipe> findByClassName (String className) {
        PizzaRecipe recipe = storage.read(className);
        return Optional.ofNullable(recipe);
    }

    public long count() {
        return storage.size();
    }

    public PizzaRecipe save(PizzaRecipe recipe) {
        storage.put(recipe.getClassName(), recipe);
        return recipe;
    }

    public PizzaRecipe delete(PizzaRecipe recipe) {
        return storage.remove(recipe.getClassName());
    }
}
