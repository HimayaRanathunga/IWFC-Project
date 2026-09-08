package com.iwfc.pattern.behavioural;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer pattern (Subject): keeps track of subscribers and pushes
 * notifications to all of them. Decouples the services that raise events
 * (e.g. MaintenanceService) from how each role displays them.
 */
public class NotificationCenter {

    private final List<NotificationObserver> subscribers = new ArrayList<>();

    public void registerObserver(NotificationObserver observer) {
        if (!subscribers.contains(observer)) {
            subscribers.add(observer);
        }
    }

    public void removeObserver(NotificationObserver observer) {
        subscribers.remove(observer);
    }

    public void notifyAll(String message) {
        for (NotificationObserver subscriber : subscribers) {
            subscriber.onNotify(message);
        }
    }
}
