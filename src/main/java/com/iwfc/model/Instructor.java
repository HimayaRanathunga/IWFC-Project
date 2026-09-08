package com.iwfc.model;

public class Instructor extends User {

    public Instructor(String username, String name) {
        super(username, name, Role.INSTRUCTOR);
    }

    @Override
    public void onNotify(String message) {
        System.out.println("[Instructor notice for " + getUsername() + "] " + message);
    }
}
