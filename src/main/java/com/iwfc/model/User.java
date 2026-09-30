package com.iwfc.model;

import com.iwfc.pattern.behavioural.NotificationObserver;

import java.util.Objects;

/**
 * [OOP Principles] Base class of the user hierarchy (Administrator, Instructor, Member).
 * Also plays the Observer role, so any user can receive notifications.
 *
 * Abstraction/Encapsulation: identity fields are private and immutable after
 * construction, exposed only through getters. Polymorphism: subclasses
 * override {@link #onNotify(String)} to react differently to the same event.
 */
public abstract class User implements NotificationObserver {

    private final String username;
    private final String name;
    private final Role role;

    // Protected constructor: only subclasses can create a User (the class is abstract anyway).
    protected User(String username, String name, Role role) {
        this.username = username;
        this.name = name;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    // Abstraction: no body here, every subclass must define its own reaction.
    @Override
    public abstract void onNotify(String message);

    // equals/hashCode contract: two users are equal if the username is the same,
    // and hashCode uses the same field so HashMap/HashSet work correctly.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return role + ":" + username + " (" + name + ")";
    }
}
