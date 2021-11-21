package com.infoworks.lab.util.states.client.machine;

import com.infoworks.lab.util.states.client.users.User;
import com.infoworks.lab.util.states.context.StateContext;

/**
 * You can also apply this approach to objects. Imagine that we have a Document class.
 * A document can be in one of three states: Draft, Moderation and Published.
 * The publish method of the document works a little bit differently in each state:
 *
 *     In Draft, it moves the document to moderation.
 *     In Moderation, it makes the document public, but only if the current user is an administrator.
 *     In Published, it doesn’t do anything at all.
 */

public interface iDocument extends StateContext {
    void publish();
    void render();
    void setUserToCurrentState(User user);
}
