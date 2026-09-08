package com.iwfc.model;

import com.iwfc.pattern.behavioural.NotificationObserver;

import java.util.Objects;

/**
 * Abstraction/Encapsulation: identity fields are private and immutable after
 * construction, exposed only through getters. Polymorphism: subclasses
 * override {@link #onNotify(String)} to react differently to the same event.
 */
public abstract class User implements NotificationObserver {

    private final String username;
    private final String name;
    private final Role role;

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

    @Override
    public abstract void onNotify(String message);

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
