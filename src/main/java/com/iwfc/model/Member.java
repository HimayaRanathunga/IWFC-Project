package com.iwfc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * [OOP Principles] Inheritance: a Member is a User with the MEMBER role.
 * Unlike the other roles, a Member also keeps its own list of notifications.
 */
public class Member extends User {

    // Encapsulation: private list, never handed out directly.
    private final List<String> notifications = new ArrayList<>();

    public Member(String username, String name) {
        super(username, name, Role.MEMBER);
    }

    // Polymorphism: overrides User.onNotify, members also store the message.
    @Override
    public void onNotify(String message) {
        notifications.add(message);
        System.out.println("[New notification for " + getUsername() + "] " + message);
    }

    // Defensive copy: List.copyOf returns an unmodifiable snapshot, so callers cannot change our list.
    public List<String> getNotifications() {
        return List.copyOf(notifications);
    }
}
