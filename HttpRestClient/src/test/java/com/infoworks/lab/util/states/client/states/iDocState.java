package com.infoworks.lab.util.states.client.states;

import com.infoworks.lab.util.states.client.users.User;
import com.infoworks.lab.util.states.context.State;

public interface iDocState extends State {
    void setUser(User user);
    void publish();
    void render();
}
