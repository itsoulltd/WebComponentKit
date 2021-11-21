package com.infoworks.lab.util.states.context;

public interface StateContext {
    void changeState(State state);

    /**
     * Inspired by Apple's GamePlayKit StateMachine API:
     * @return
     */
    default State currentState() {return null;}
    default boolean isCurrentState(Class<? extends State> type) {return (currentState() == null) ? false : type.isInstance(currentState());}
    default boolean canEnterState(Class<? extends State> type) {return (currentState() == null) ? true : currentState().isValidNextState(type);}
    default Class<? extends State>[] states() {return new Class[0];}
    default void addStates(Class<? extends State>...states) {/**/}
    default boolean enter(Class<? extends State> state) {return false;}
    default boolean moveNext() {return false;}
}
