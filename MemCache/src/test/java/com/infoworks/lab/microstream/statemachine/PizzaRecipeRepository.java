package com.infoworks.lab.microstream.statemachine;

import com.infoworks.lab.microstream.MicroDataStore;
import com.it.soul.lab.data.simple.SimpleDataSource;

import java.util.Optional;

public class PizzaRecipeRepository {

    private final SimpleDataSource<String, PizzaRecipe> storage;

    public PizzaRecipeRepository(String location) {
        storage = new MicroDataStore<>(location + "/pizzaRecipes");
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
