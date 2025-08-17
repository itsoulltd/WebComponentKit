package com.infoworks.lab.statemachine;

import com.infoworks.lab.microstream.MicroDataStore;
import com.infoworks.lab.statemachine.orders.states.BurningOnOven;
import com.infoworks.lab.statemachine.orders.states.Confirmed;
import com.infoworks.lab.statemachine.orders.states.Placed;
import com.infoworks.lab.statemachine.orders.states.ReadyToServe;
import com.infoworks.lab.statemachine.pizzas.Pizza;
import com.infoworks.lab.statemachine.pizzas.ThinCrustPizza;
import com.infoworks.lab.statemachine.toppings.Toppings;
import com.infoworks.lab.util.states.StateMachine;
import com.it.soul.lab.data.simple.SimpleDataSource;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PizzaService implements AutoCloseable{

    private static Logger LOG = Logger.getLogger("PizzaService");
    private final PizzaRecipeRepository repository;
    private SimpleDataSource<String, StateMachine> machines;
    private SimpleDataSource<String, Pizza> pizzas;

    public PizzaService(String location
            , SimpleDataSource<String, StateMachine> machines
            , SimpleDataSource<String, Pizza> pizzas) {
        this.repository = new PizzaRecipeRepository(location);
        this.machines = machines;
        this.pizzas = pizzas;
    }

    public PizzaRecipeRepository getRepository() {
        return repository;
    }

    public StateMachine getMachineByOrderId(String orderId) {
        return machines.read(orderId);
    }

    public Pizza createPizza(String type, String...toppings) throws RuntimeException{
        Pizza pizza = createPizza(type);
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

    private Pizza createPizza(String type) {
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

    public Pizza addToppings(Order order, Class<? extends Toppings> topping) throws RuntimeException {
        if (order.getOrderId() == null) throw new RuntimeException("OrderId is null!");
        Pizza pizza = pizzas.read(order.getOrderId());
        if (pizza == null){
            pizza = createPizza(order.getPizzaClassName()
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

    public Order changeState(Order order) {
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

    public Order createOrder(Class<? extends Pizza> pizza) {
        Order order = new Order();
        order.setPizzaClassName(pizza.getName());
        return changeState(order);
    }

    public boolean canAddToppings(Order order) {
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

    @Override
    public void close() throws Exception {
        repository.close();
        if (pizzas instanceof MicroDataStore){
            try {
                ((MicroDataStore<String, Pizza>) pizzas).close();
                LOG.info("InMem-Pizzas Storage Save Successful");
            } catch (Exception e) {
                LOG.log(Level.WARNING, e.getMessage(), e);
            }
        }
        if (machines instanceof MicroDataStore){
            try {
                ((MicroDataStore<String, StateMachine>) machines).close();
                LOG.info("InMem-Statemachine Storage Save Successful");
            } catch (Exception e) {
                LOG.log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
}
