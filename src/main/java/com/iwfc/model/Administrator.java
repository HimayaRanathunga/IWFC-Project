package com.iwfc.model;

public class Administrator extends User {

    public Administrator(String username, String name) {
        super(username, name, Role.ADMINISTRATOR);
    }

    @Override
    public void onNotify(String message) {
        System.out.println("[ADMIN ALERT -> " + getUsername() + "] " + message);
    }
}
