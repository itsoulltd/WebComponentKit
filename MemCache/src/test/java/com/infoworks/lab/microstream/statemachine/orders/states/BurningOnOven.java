package com.infoworks.lab.microstream.statemachine.orders.states;

import com.infoworks.lab.util.states.context.State;

public class BurningOnOven extends OrderState {

    @Override
    public boolean isValidNextState(Class<? extends State> aClass) {
        return ReadyToServe.class.isAssignableFrom(aClass);
    }
}
