package com.infoworks.lab.util.states.client.users;

public class User {
    private Role role;

    public User(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
