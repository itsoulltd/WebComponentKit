package com.infoworks.lab.util.states.client.machine;

import com.infoworks.lab.util.states.StateMachine;
import com.infoworks.lab.util.states.client.states.iDocState;
import com.infoworks.lab.util.states.client.users.User;
import com.infoworks.lab.util.states.context.State;

public class Document extends StateMachine implements iDocument{

    public Document(Class<? extends State>... states) {
        super(states);
    }

    public Document(State... states) {
        super(states);
    }

    @Override
    public void setUserToCurrentState(User user){
        if (currentState() != null){
            State state = currentState();
            if (state instanceof iDocState){
                ((iDocState) state).setUser(user);
            }
        }
    }

    @Override
    public void changeState(State state) {
        super.changeState(state);
    }

    @Override
    public void publish() {
        State state = currentState();
        if (state instanceof iDocState){
            ((iDocState)state).publish();
        }
    }

    @Override
    public void render() {
        State state = currentState();
        if (state instanceof iDocState){
            ((iDocState)state).render();
        }
    }
}
