package com.infoworks.lab.microstream.statemachine.pizzas;

import java.math.BigDecimal;

public class ThinCrustPizza extends AbstractPizza {

    public ThinCrustPizza(){}

    @Override
    public BigDecimal getCost() {
        return new BigDecimal(getRecipe().getPrice());
    }
}
