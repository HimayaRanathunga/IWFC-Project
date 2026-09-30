package com.iwfc.model;

/**
 * [OOP Principles] Inheritance: an Instructor is a User with the INSTRUCTOR role.
 * Instructors create and run sessions.
 */
public class Instructor extends User {

    public Instructor(String username, String name) {
        // Inheritance: super() sets the fields that are stored in User.
        super(username, name, Role.INSTRUCTOR);
    }

    // Polymorphism: overrides User.onNotify, instructors see a notice style message.
    @Override
    public void onNotify(String message) {
        System.out.println("[Instructor notice for " + getUsername() + "] " + message);
    }
}
