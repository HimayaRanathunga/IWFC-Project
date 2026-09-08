package com.iwfc.pattern.behavioural;

/**
 * Observer pattern: any subscriber that wants to react to system-wide
 * notifications (e.g. a maintenance status change) implements this.
 */
public interface NotificationObserver {
    void onNotify(String message);
}
