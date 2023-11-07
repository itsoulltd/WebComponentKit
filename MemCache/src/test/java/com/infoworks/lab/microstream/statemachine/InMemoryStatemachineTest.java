package com.infoworks.lab.microstream.statemachine;

import com.infoworks.lab.microstream.MicroDataStore;
import com.infoworks.lab.microstream.statemachine.pizzas.CheeseCrustPizza;
import com.infoworks.lab.microstream.statemachine.pizzas.Pizza;
import com.infoworks.lab.microstream.statemachine.pizzas.ThickCrustPizza;
import com.infoworks.lab.microstream.statemachine.pizzas.ThinCrustPizza;
import com.infoworks.lab.microstream.statemachine.toppings.*;
import com.infoworks.lab.util.states.StateMachine;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InMemoryStatemachineTest {

    String location = "target/MicroStream/InMemoryStatemachineTest";
    PizzaService service;

    @Before
    public void before() {
        service = new PizzaService(location);
        //Adding Recipes:
        service.getRepository().save(new PizzaRecipe("1", "12.00", ThinCrustPizza.class.getName(), "Thin Crust Pizza"));
        service.getRepository().save(new PizzaRecipe("2", "15.00", ThickCrustPizza.class.getName(), "Thick Crust Pizza"));
        service.getRepository().save(new PizzaRecipe("3", "20.20", CheeseCrustPizza.class.getName(), "Cheese Crust Pizza"));
        //
        service.getRepository().save(new PizzaRecipe("4", "1.00", Cheese.class.getName(), "Cheese"));
        service.getRepository().save(new PizzaRecipe("5", "2.00", Mushroom.class.getName(), "Mushroom"));
        service.getRepository().save(new PizzaRecipe("6", "1.50", Pepperoni.class.getName(), "Pepperoni"));
        service.getRepository().save(new PizzaRecipe("7", "2.70", Sausage.class.getName(), "Sausage"));
        //
    }

    @After
    public void after() {
        service.getRepository().clear();
    }

    @Test
    public void statemachineTest() {
        //
        SimpleDataSource<String, Pizza> pizzas = new MicroDataStore<>(location + "/pizza");
        SimpleDataSource<String, StateMachine> machines = new MicroDataStore<>(location + "/statemachine");
        //TODO
    }

    private Class<? extends Pizza> whichCrust(String crustName) {
        if (crustName.toLowerCase().startsWith("thin")){
            return ThinCrustPizza.class;
        } else if (crustName.toLowerCase().startsWith("cheese")){
            return CheeseCrustPizza.class;
        } else {
            return ThickCrustPizza.class;
        }
    }

    private Class<? extends Toppings> whichToppings(String toppings) {
        if (toppings.toLowerCase().equalsIgnoreCase(Sausage.class.getSimpleName())){
            return Sausage.class;
        } else if (toppings.toLowerCase().equalsIgnoreCase(Pepperoni.class.getSimpleName())){
            return Pepperoni.class;
        } else if (toppings.toLowerCase().equalsIgnoreCase(Mushroom.class.getSimpleName())){
            return Mushroom.class;
        } else {
            return Cheese.class;
        }
    }

}
