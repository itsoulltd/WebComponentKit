package com.infoworks.lab.statemachine.orders.states;

import com.infoworks.lab.util.states.context.State;

public class Confirmed extends OrderState {

    @Override
    public boolean isValidNextState(Class<? extends State> aClass) {
        return BurningOnOven.class.isAssignableFrom(aClass);
    }
}
