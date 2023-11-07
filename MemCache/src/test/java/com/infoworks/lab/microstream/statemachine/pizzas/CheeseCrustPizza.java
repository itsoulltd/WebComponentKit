package com.infoworks.lab.microstream.statemachine.pizzas;

import java.math.BigDecimal;

public class CheeseCrustPizza extends AbstractPizza {

    public CheeseCrustPizza() {}

    @Override
    public BigDecimal getCost() {
        return new BigDecimal(getRecipe().getPrice());
    }
}
