package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for EventListener - testing the component in isolation.
 * This demonstrates basic unit testing without dependency injection.
 */
class EventListenerTest {

    private EventListener eventListener;

    @BeforeEach
    void setUp() {
        eventListener = new EventListener();
    }

    @Test
    void shouldReceiveEvent() {
        // Given
        DemoEvent event = new DemoEvent("Test message");

        // When
        eventListener.onEvent(event);

        // Then
        assertEquals(1, eventListener.getEventCount());
        assertEquals("Test message", eventListener.getReceivedEvents().get(0).getMessage());
    }

    @Test
    void shouldReceiveMultipleEvents() {
        // Given
        DemoEvent event1 = new DemoEvent("First message");
        DemoEvent event2 = new DemoEvent("Second message");

        // When
        eventListener.onEvent(event1);
        eventListener.onEvent(event2);

        // Then
        assertEquals(2, eventListener.getEventCount());
        assertEquals("First message", eventListener.getReceivedEvents().get(0).getMessage());
        assertEquals("Second message", eventListener.getReceivedEvents().get(1).getMessage());
    }

    @Test
    void shouldClearEvents() {
        // Given
        eventListener.onEvent(new DemoEvent("Test"));
        assertEquals(1, eventListener.getEventCount());

        // When
        eventListener.clearEvents();

        // Then
        assertEquals(0, eventListener.getEventCount());
        assertTrue(eventListener.getReceivedEvents().isEmpty());
    }
}