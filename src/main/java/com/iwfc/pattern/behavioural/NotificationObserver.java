package com.iwfc.pattern.behavioural;

/**
 * [Behavioural Pattern - Observer]
 * Observer pattern (Observer interface): any subscriber that wants to react to
 * system-wide notifications (e.g. a maintenance status change) implements this.
 * It has one abstract method, so it is also a functional interface: a lambda or
 * method reference can be used as an observer (User implements it too).
 */
public interface NotificationObserver {
    void onNotify(String message);
}
