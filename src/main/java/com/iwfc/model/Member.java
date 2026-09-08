package com.iwfc.model;

import java.util.ArrayList;
import java.util.List;

public class Member extends User {

    private final List<String> notifications = new ArrayList<>();

    public Member(String username, String name) {
        super(username, name, Role.MEMBER);
    }

    @Override
    public void onNotify(String message) {
        notifications.add(message);
        System.out.println("[New notification for " + getUsername() + "] " + message);
    }

    public List<String> getNotifications() {
        return List.copyOf(notifications);
    }
}
