package com.iwfc.pattern;

import com.iwfc.model.Member;
import com.iwfc.pattern.behavioural.NotificationCenter;
import com.iwfc.pattern.behavioural.NotificationObserver;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * [Unit Testing] Tests for the Observer pattern (Behavioural pattern requirement).
 */
class NotificationCenterTest {

    // Verifies: an observer registered with the subject receives the published message.
    @Test
    void registerObserver_receivesNotification() {
        NotificationCenter center = new NotificationCenter();
        List<String> received = new ArrayList<>();
        // Method reference (received::add) used as the NotificationObserver, possible because the interface has one method.
        NotificationObserver observer = received::add;

        center.registerObserver(observer);
        center.notifyAll("Test message");

        assertEquals(1, received.size());
        assertEquals("Test message", received.get(0));
    }

    // Verifies: Polymorphic dispatch - the center only knows the NotificationObserver type.
    @Test
    void notifyAll_invokesOverriddenOnNotifyPerRole() {
        // Member overrides onNotify() to record the message in its own state -
        // NotificationCenter calls onNotify() through the NotificationObserver
        // reference without knowing it is really a Member (polymorphic dispatch).
        NotificationCenter center = new NotificationCenter();
        Member member = new Member("mem1", "Mary Member");
        center.registerObserver(member);

        center.notifyAll("Session cancelled");

        assertTrue(member.getNotifications().contains("Session cancelled"));
    }
}
