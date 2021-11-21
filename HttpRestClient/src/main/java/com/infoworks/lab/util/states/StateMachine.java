package com.infoworks.lab.util.states;

import com.infoworks.lab.util.states.context.State;
import com.infoworks.lab.util.states.context.StateContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StateMachine implements StateContext {

    private static Logger LOG = Logger.getLogger(StateMachine.class.getSimpleName());
    private final Map<String, State> stateMap = new HashMap<>();
    private final Class<? extends State>[] states;
    private State activeState;

    public StateMachine(Class<? extends State>...states) {
        this.states = states;
        for (Class stateType: states) {
            try {
                State state = (State) stateType.newInstance();
                stateMap.put(stateType.getName(), state);
            } catch (InstantiationException e) {
                LOG.log(Level.WARNING, e.getMessage());
            } catch (IllegalAccessException e) {
                LOG.log(Level.WARNING, e.getMessage());
            }
        }
    }

    public StateMachine(State...states) {
        List<Class<? extends State>> items = new ArrayList<>(states.length);
        for (State state: states) {
            try {
                String key = state.getClass().getName();
                stateMap.put(key, state);
                items.add(state.getClass());
            } catch (Exception e) {
                LOG.log(Level.WARNING, e.getMessage());
            }
        }
        this.states = items.toArray(new Class[0]);
    }

    @Override
    public Class<? extends State>[] states() {
        return states;
    }

    @Override
    public State currentState() {
        return activeState;
    }

    @Override
    public void changeState(State state) {
        enter(state.getClass());
    }

    @Override
    public synchronized boolean enter(Class<? extends State> state) {
        State to = stateMap.get(state.getName());
        boolean shouldMoveToNext = (activeState == null) ? true : activeState.isValidNextState(state);
        if (shouldMoveToNext) {
            if (activeState != null){
                activeState.willExit(to);
                activeState.setContext(null);
            }
            State from = activeState;
            activeState = to;
            if (activeState != null){
                activeState.setContext(this);
                activeState.didEnter(from);
            }
        }
        return shouldMoveToNext;
    }

    @Override
    public boolean moveNext() {
        Class state = null;
        if (currentState() != null) {
            State _state = currentState();
            //find current_state's index:
            int index = 0;
            int length = states.length;
            for (Class xState : states) {
                if (xState.getSimpleName().equalsIgnoreCase(_state.getClass().getSimpleName())){
                    break;
                }
                index++;
            }
            //just pick the next state of the current_state:
            if(index < (length - 1)) state = states()[index+1];
        } else{
            state = states()[0];
        }
        return (state != null) ? enter(state) : false;
    }

}
