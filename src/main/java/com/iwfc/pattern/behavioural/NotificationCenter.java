package com.iwfc.pattern.behavioural;

import java.util.ArrayList;
import java.util.List;

/**
 * [Behavioural Pattern - Observer]
 * Observer pattern (Subject): keeps track of subscribers and pushes
 * notifications to all of them. Decouples the services that raise events
 * (e.g. MaintenanceService) from how each role displays them.
 * Note: the subscriber list is a plain ArrayList, so this class is not thread-safe.
 */
public class NotificationCenter {

    // Dependency inversion: the subject only knows the NotificationObserver interface,
    // never the concrete User classes. Generics: a typed list of observers.
    private final List<NotificationObserver> subscribers = new ArrayList<>();

    /** Observer: subscribe an observer (duplicates are ignored). */
    public void registerObserver(NotificationObserver observer) {
        if (!subscribers.contains(observer)) {
            subscribers.add(observer);
        }
    }

    /** Observer: unsubscribe an observer. */
    public void removeObserver(NotificationObserver observer) {
        subscribers.remove(observer);
    }

    /**
     * Observer: publish a message to every subscriber.
     * This is an overload with a String parameter; it is different from Object.notifyAll()
     * (which is about threads and takes no arguments).
     */
    public void notifyAll(String message) {
        for (NotificationObserver subscriber : subscribers) {
            // Polymorphic dispatch: each subscriber's own onNotify runs (Administrator, Instructor, Member).
            subscriber.onNotify(message);
        }
    }
}
