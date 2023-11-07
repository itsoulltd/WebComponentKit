package com.infoworks.lab.microstream.statemachine;

import com.infoworks.lab.microstream.MicroDataStore;
import com.infoworks.lab.microstream.statemachine.orders.states.BurningOnOven;
import com.infoworks.lab.microstream.statemachine.orders.states.Confirmed;
import com.infoworks.lab.microstream.statemachine.orders.states.Placed;
import com.infoworks.lab.microstream.statemachine.orders.states.ReadyToServe;
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

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class InMemoryStatemachineTest {

    String location = "target/MicroStream/InMemoryStatemachineTest";
    PizzaRecipeRepository repository;

    @Before
    public void before() {
        repository = new PizzaRecipeRepository();
    }

    @After
    public void after() {
        repository.clear();
    }

    @Test
    public void statemachineTest() {
        //
        SimpleDataSource<String, Pizza> pizzas = new MicroDataStore<>(location + "/pizza");
        SimpleDataSource<String, StateMachine> machines = new MicroDataStore<>(location + "/statemachine");
        //TODO
    }

    private Pizza addToppings(Order order, Class<? extends Toppings> topping, SimpleDataSource<String, Pizza> pizzas) throws RuntimeException {
        if (order.getOrderId() == null) throw new RuntimeException("OrderId is null!");
        Pizza pizza = pizzas.read(order.getOrderId());
        if (pizza == null){
            pizza = createPizza(order.getPizzaClassName()
                    , repository
                    , order.getToppingClassNames().toArray(new String[0]));
        }
        //
        if (topping != null){
            try {
                //Configuring Order and Toppings with Pizza:
                Toppings toppings = topping.getDeclaredConstructor().newInstance();
                //Now Check for Recipe of a Topping:
                Optional<PizzaRecipe> optRecipe = repository.findByClassName(topping.getName());
                if (optRecipe.isPresent()){
                    toppings.setRecipe(optRecipe.get());
                }
                //
                toppings.setPizza(pizza);
                pizza = toppings;
                order.getToppingClassNames().add(topping.getName());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        pizzas.put(order.getOrderId(), pizza);
        order.setPrice(pizza.printedCost());
        order.setDescription(pizza.getDescription());
        return pizza;
    }

    private Pizza createPizza(String type, PizzaRecipeRepository repository, String...toppings) throws RuntimeException{
        Pizza pizza = createPizza(type, repository);
        //After recreating Pizza from name lets add those toppings:
        if (toppings != null && toppings.length > 0){
            for (String savedToppingClassName : toppings){
                try {
                    Class<? extends Toppings> savedToppingClass = (Class<Toppings>) Class.forName(savedToppingClassName);
                    Toppings savedTopping = savedToppingClass.getDeclaredConstructor().newInstance();
                    //Now Check for Recipe of a saved Topping:
                    Optional<PizzaRecipe> optRecipe = repository.findByClassName(savedToppingClassName);
                    if (optRecipe.isPresent()){
                        savedTopping.setRecipe(optRecipe.get());
                    }
                    //
                    savedTopping.setPizza(pizza);
                    pizza = savedTopping;
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }
        return pizza;
    }

    private Pizza createPizza(String type, PizzaRecipeRepository repository) {
        if (type != null && !type.isEmpty() ){
            try {
                Class aClass = Class.forName(type);
                Pizza pizza = (Pizza) aClass.getDeclaredConstructor().newInstance();
                //Now Check for Recipe:
                Optional<PizzaRecipe> optRecipe = repository.findByClassName(type);
                if (optRecipe.isPresent()){
                    pizza.setRecipe(optRecipe.get());
                }
                return pizza;
            } catch (ClassNotFoundException | IllegalAccessException
                    | InstantiationException | NoSuchMethodException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return new ThinCrustPizza();
    }

    private Order changeState(Order order, SimpleDataSource<String, StateMachine> machines) {
        if (order.getOrderId() == null) return order;
        StateMachine machine = machines.read(order.getOrderId());
        if (machine == null){
            machine = createMachine(order.getStateClassName());
            machines.put(order.getOrderId(), machine);
        }
        machine.moveNext();
        order.setStateClassName(machine.currentState().getClass().getName());
        //If-Order-Reached-To-ReadyToServe: remove from all in-mem-store
        if (machine.isCurrentState(ReadyToServe.class)) {
            machines.remove(order.getOrderId());
        }
        return order;
    }

    private boolean canAddToppings(Order order, SimpleDataSource<String, StateMachine> machines) {
        if (order.getOrderId() == null) return false;
        StateMachine machine = machines.read(order.getOrderId());
        return machine.isCurrentState(Placed.class);
    }

    private StateMachine createMachine(String lastKnownState) {
        StateMachine machine = new StateMachine(Placed.class
                , Confirmed.class
                , BurningOnOven.class
                , ReadyToServe.class);
        //
        if (lastKnownState != null && !lastKnownState.isEmpty()){
            //After re-create statemachine lets move to last saved state:
            do{
                machine.moveNext();
            } while (!machine.currentState().getClass()
                    .getName().equalsIgnoreCase(lastKnownState));
        }
        return machine;
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
