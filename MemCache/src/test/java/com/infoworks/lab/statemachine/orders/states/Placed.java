package com.infoworks.lab.statemachine.orders.states;

import com.infoworks.lab.util.states.context.State;

public class Placed extends OrderState {

    @Override
    public boolean isValidNextState(Class<? extends State> aClass) {
        return Confirmed.class.isAssignableFrom(aClass);
    }
}
