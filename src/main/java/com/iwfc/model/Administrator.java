package com.iwfc.model;

/**
 * [OOP Principles] Inheritance: an Administrator is a User with the ADMINISTRATOR role.
 * Administrators can manage equipment and maintenance (checked by the facade).
 */
public class Administrator extends User {

    public Administrator(String username, String name) {
        // Inheritance: super() sets the fields that are stored in User.
        super(username, name, Role.ADMINISTRATOR);
    }

    // Polymorphism: overrides User.onNotify, admins see an alert style message.
    @Override
    public void onNotify(String message) {
        System.out.println("[ADMIN ALERT -> " + getUsername() + "] " + message);
    }
}
