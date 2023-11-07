package com.infoworks.lab.microstream.statemachine.orders.states;

import com.infoworks.lab.util.states.context.State;
import com.infoworks.lab.util.states.context.StateContext;

public abstract class OrderState implements State {

    private StateContext context;

    @Override
    public StateContext getContext() {
        return context;
    }

    @Override
    public void setContext(StateContext stateContext) {
        this.context = stateContext;
    }

}
