package com.infoworks.lab.microstream;

import com.infoworks.lab.microstream.statemachine.Order;
import com.infoworks.lab.microstream.statemachine.PizzaRecipe;
import com.infoworks.lab.microstream.statemachine.PizzaService;
import com.infoworks.lab.microstream.statemachine.orders.states.Confirmed;
import com.infoworks.lab.microstream.statemachine.orders.states.Placed;
import com.infoworks.lab.microstream.statemachine.pizzas.CheeseCrustPizza;
import com.infoworks.lab.microstream.statemachine.pizzas.Pizza;
import com.infoworks.lab.microstream.statemachine.pizzas.ThickCrustPizza;
import com.infoworks.lab.microstream.statemachine.pizzas.ThinCrustPizza;
import com.infoworks.lab.microstream.statemachine.toppings.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InMemoryStatemachineTest {

    String location = "target/MicroStream/InMemoryStatemachineTest";
    PizzaService service;

    @Before
    public void before() {
        service = new PizzaService(location);
        //If no seed data already added:
        if (service.getRepository().count() <= 0) {
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
    }

    @After
    public void after() throws Exception {
        //service.getRepository().clear();
        service.close();
    }

    @Test
    public void statemachineTest() {
        //
        //SimpleDataSource<String, Pizza> pizzas = new MicroDataStore<>(location + "/pizza");
        //SimpleDataSource<String, StateMachine> machines = new MicroDataStore<>(location + "/statemachine");
        //TODO
        Order newOrder = service.createOrder(whichCrust("thick"));
        newOrder.setCustomerEmail("m.towhid@gmail.com");
        service.addToppings(newOrder, null);
        //
        //Assert: current price
        Assert.assertEquals("15.00", newOrder.getPrice());
        System.out.println("Price: " + newOrder.getPrice());
        //Assert: state
        Assert.assertEquals(Placed.class.getName(), newOrder.getStateClassName());
        System.out.println("State: " + newOrder.getStateClassName());

        service.changeState(newOrder);

        Assert.assertEquals(Confirmed.class.getName(), newOrder.getStateClassName());
        System.out.println("State: " + newOrder.getStateClassName());
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
