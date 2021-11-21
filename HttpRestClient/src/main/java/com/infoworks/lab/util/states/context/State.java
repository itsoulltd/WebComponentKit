package com.infoworks.lab.util.states.context;

public interface State {
    StateContext getContext();
    void setContext(StateContext context);

    /**
     * Inspired by Apple's GamePlayKit State API:
     * @return
     */
    boolean isValidNextState(Class<? extends State> sType);
    default void didEnter(State from) {/**/}
    default void willExit(State to) {/**/}
}
