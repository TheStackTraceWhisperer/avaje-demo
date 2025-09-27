package com.example.demo;

import io.avaje.inject.BeanScope;
import io.avaje.inject.test.InjectTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Component test demonstrating CORRECT avaje-inject test setup.
 * This test shows how to properly test components with dependency injection.
 * 
 * Key points:
 * 1. Use @InjectTest to enable avaje-inject testing
 * 2. Use BeanScope.builder().build() to create test context
 * 3. Clean up resources after each test
 */
@InjectTest
class EventSystemIntegrationTest {

    private BeanScope beanScope;

    @AfterEach
    void cleanup() {
        if (beanScope != null) {
            beanScope.close();
        }
    }

    @Test
    void shouldInjectDependenciesCorrectly() {
        // Given - Create avaje-inject context
        beanScope = BeanScope.builder().build();
        
        EventProducer producer = beanScope.get(EventProducer.class);
        EventListener listener = beanScope.get(EventListener.class);

        // Then - Verify beans are properly injected
        assertNotNull(producer, "EventProducer should be injected");
        assertNotNull(listener, "EventListener should be injected");
    }

    @Test
    void shouldHandleEventFlow() {
        // Given - Create avaje-inject context
        beanScope = BeanScope.builder().build();
        
        EventProducer producer = beanScope.get(EventProducer.class);
        EventListener listener = beanScope.get(EventListener.class);

        // Ensure clean state
        listener.clearEvents();

        // When - Produce an event
        producer.produceEvent("Integration test message");

        // Then - Verify event was received
        assertEquals(1, listener.getEventCount());
        assertEquals("Integration test message", listener.getReceivedEvents().get(0).getMessage());
    }

    @Test
    void shouldHandleMultipleEvents() {
        // Given - Create avaje-inject context
        beanScope = BeanScope.builder().build();
        
        EventProducer producer = beanScope.get(EventProducer.class);
        EventListener listener = beanScope.get(EventListener.class);

        // Ensure clean state
        listener.clearEvents();

        // When - Produce multiple events
        producer.produceEvents("Event 1", "Event 2", "Event 3");

        // Then - Verify all events were received
        assertEquals(3, listener.getEventCount());
        assertEquals("Event 1", listener.getReceivedEvents().get(0).getMessage());
        assertEquals("Event 2", listener.getReceivedEvents().get(1).getMessage());
        assertEquals("Event 3", listener.getReceivedEvents().get(2).getMessage());
    }

    @Test
    void shouldMaintainSingletonBehavior() {
        // Given - Create avaje-inject context
        beanScope = BeanScope.builder().build();

        // When - Get the same bean multiple times
        EventListener listener1 = beanScope.get(EventListener.class);
        EventListener listener2 = beanScope.get(EventListener.class);

        // Then - Should be the same instance (singleton)
        assertSame(listener1, listener2, "EventListener should be singleton");
    }
}