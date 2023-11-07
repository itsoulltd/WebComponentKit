package com.infoworks.lab.microstream.statemachine.orders.states;

import com.infoworks.lab.util.states.context.State;

public class ReadyToServe extends OrderState {

    @Override
    public boolean isValidNextState(Class<? extends State> aClass) {
        return false;
    }
}
