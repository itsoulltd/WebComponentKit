package com.infoworks.lab.statemachine;

import com.infoworks.lab.microstream.MicroDataStore;
import com.it.soul.lab.data.simple.SimpleDataSource;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PizzaRecipeRepository implements AutoCloseable {

    private static Logger LOG = Logger.getLogger("PizzaRecipeRepository");
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

    @Override
    public void close() throws Exception {
        if (storage instanceof MicroDataStore){
            try {
                ((MicroDataStore<String, PizzaRecipe>) storage).close();
                LOG.info("InMem-PizzaRecipe Storage Save Successful");
            } catch (Exception e) {
                LOG.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
}
