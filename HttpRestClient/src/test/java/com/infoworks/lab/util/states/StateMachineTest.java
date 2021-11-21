package com.infoworks.lab.util.states;

import com.infoworks.lab.util.states.client.machine.Document;
import com.infoworks.lab.util.states.client.states.Draft;
import com.infoworks.lab.util.states.client.states.Moderation;
import com.infoworks.lab.util.states.client.states.Published;
import com.infoworks.lab.util.states.client.users.Role;
import com.infoworks.lab.util.states.client.users.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StateMachineTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void basicTest(){
        //Both way its same: but mix mode did not implemented:
        Document docs = new Document(Draft.class, Moderation.class, Published.class);
        //Document docs = new Document(new Moderation(), new Draft(), new Published());
        //
        System.out.println("---------Draft & AUTHOR---------");
        docs.enter(Draft.class);
        docs.setUserToCurrentState(new User(Role.AUTHOR));
        docs.render();
        docs.publish();
        //
        System.out.println("---------Moderation & MODERATOR---------");
        docs.enter(Moderation.class);
        docs.setUserToCurrentState(new User(Role.MODERATOR));
        docs.render();
        docs.publish();
        //
        System.out.println("---------Published & PUBLISHER---------");
        docs.enter(Published.class);
        docs.setUserToCurrentState(new User(Role.PUBLISHER));
        docs.render();
        docs.publish();
        //
        System.out.println("---------Draft & MODERATOR---------");
        docs.enter(Draft.class);
        docs.setUserToCurrentState(new User(Role.MODERATOR));
        docs.render();
        docs.publish();
    }

    @Test
    public void moveNextTest(){
        //Both way its same: but mix mode did not implemented:
        Document docs = new Document(Draft.class, Moderation.class, Published.class);
        //Document docs = new Document(new Moderation(), new Draft(), new Published());
        //
        System.out.println("---------Draft & AUTHOR---------");
        docs.moveNext();
        docs.setUserToCurrentState(new User(Role.AUTHOR));
        docs.render();
        docs.publish();
        //
        System.out.println("---------Moderation & MODERATOR---------");
        docs.moveNext();
        docs.setUserToCurrentState(new User(Role.MODERATOR));
        docs.render();
        docs.publish();
        //
        System.out.println("---------Published & PUBLISHER---------");
        docs.moveNext();
        docs.setUserToCurrentState(new User(Role.PUBLISHER));
        docs.render();
        docs.publish();
        //
        System.out.println("---------Draft & MODERATOR---------");
        docs.moveNext();
        docs.setUserToCurrentState(new User(Role.MODERATOR));
        docs.render();
        docs.publish();
    }
}