package com.example.demo;

import com.example.demo.interceptor.TimingMetrics;
import io.avaje.inject.BeanScope;
import io.avaje.inject.test.InjectTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for interceptor functionality in the event system.
 * This test verifies that trace logging and timing metrics work correctly
 * with the avaje-inject dependency injection system.
 */
@InjectTest
class InterceptorIntegrationTest {

    private BeanScope beanScope;

    @AfterEach
    void cleanup() {
        if (beanScope != null) {
            beanScope.close();
        }
    }

    @Test
    void shouldRecordTimingMetricsForEventProducer() {
        // Given - Create avaje-inject context
        beanScope = BeanScope.builder().build();
        
        EventProducer producer = beanScope.get(EventProducer.class);
        EventListener listener = beanScope.get(EventListener.class);

        // Ensure clean state
        listener.clearEvents();

        // When - Produce events to trigger timing metrics
        producer.produceEvent("Test message for timing");
        producer.produceEvents("Batch message 1", "Batch message 2");

        // Then - Verify timing statistics were recorded
        TimingMetrics.TimingStat produceEventStats = producer.getTimingStats("produceEvent");
        assertNotNull(produceEventStats, "produceEvent timing stats should be recorded");
        assertEquals(3, produceEventStats.getCallCount(), "Should record all produceEvent calls");
        assertEquals(0, produceEventStats.getFailureCount(), "Should have no failures");
        assertTrue(produceEventStats.getAverageTimeMs() >= 0, "Should have non-negative average time");

        TimingMetrics.TimingStat produceEventsStats = producer.getTimingStats("produceEvents");
        assertNotNull(produceEventsStats, "produceEvents timing stats should be recorded");
        assertEquals(1, produceEventsStats.getCallCount(), "Should record produceEvents call");
        assertEquals(0, produceEventsStats.getFailureCount(), "Should have no failures");
    }

    @Test
    void shouldRecordTimingMetricsForEventListener() {
        // Given - Create avaje-inject context
        beanScope = BeanScope.builder().build();
        
        EventProducer producer = beanScope.get(EventProducer.class);
        EventListener listener = beanScope.get(EventListener.class);

        // Ensure clean state
        listener.clearEvents();

        // When - Trigger listener methods to record timing metrics
        producer.produceEvent("Test message");
        int eventCount = listener.getEventCount();
        var receivedEvents = listener.getReceivedEvents();

        // Then - Verify timing statistics were recorded for listener methods
        TimingMetrics.TimingStat onEventStats = listener.getTimingStats("onEvent");
        assertNotNull(onEventStats, "onEvent timing stats should be recorded");
        assertEquals(1, onEventStats.getCallCount(), "Should record onEvent call");
        assertEquals(0, onEventStats.getFailureCount(), "Should have no failures");

        TimingMetrics.TimingStat getEventCountStats = listener.getTimingStats("getEventCount");
        assertNotNull(getEventCountStats, "getEventCount timing stats should be recorded");
        assertEquals(1, getEventCountStats.getCallCount(), "Should record getEventCount call");

        TimingMetrics.TimingStat getReceivedEventsStats = listener.getTimingStats("getReceivedEvents");
        assertNotNull(getReceivedEventsStats, "getReceivedEvents timing stats should be recorded");
        assertEquals(1, getReceivedEventsStats.getCallCount(), "Should record getReceivedEvents call");

        // Verify the functionality still works
        assertEquals(1, eventCount);
        assertEquals(1, receivedEvents.size());
        assertEquals("Test message", receivedEvents.get(0).getMessage());
    }

    @Test
    void shouldMaintainFunctionalityWithInterceptors() {
        // Given - Create avaje-inject context
        beanScope = BeanScope.builder().build();
        
        EventProducer producer = beanScope.get(EventProducer.class);
        EventListener listener = beanScope.get(EventListener.class);

        // Ensure clean state
        listener.clearEvents();

        // When - Use the system normally
        producer.produceEvent("Intercepted message 1");
        producer.produceEvent("Intercepted message 2");
        producer.produceEvents("Batch 1", "Batch 2", "Batch 3");

        // Then - Verify functionality is preserved
        assertEquals(5, listener.getEventCount());
        var events = listener.getReceivedEvents();
        assertEquals(5, events.size());
        
        assertEquals("Intercepted message 1", events.get(0).getMessage());
        assertEquals("Intercepted message 2", events.get(1).getMessage());
        assertEquals("Batch 1", events.get(2).getMessage());
        assertEquals("Batch 2", events.get(3).getMessage());
        assertEquals("Batch 3", events.get(4).getMessage());

        // Verify interceptors didn't break the singleton behavior
        EventListener listener2 = beanScope.get(EventListener.class);
        assertSame(listener, listener2, "Should still maintain singleton behavior");
        assertEquals(5, listener2.getEventCount(), "Singleton should have same state");
    }

    @Test
    void shouldAccumulateTimingStatisticsOverMultipleCalls() {
        // Given - Create avaje-inject context
        beanScope = BeanScope.builder().build();
        
        EventProducer producer = beanScope.get(EventProducer.class);
        EventListener listener = beanScope.get(EventListener.class);

        // Ensure clean state
        listener.clearEvents();

        // When - Make multiple calls
        int numberOfCalls = 10;
        for (int i = 0; i < numberOfCalls; i++) {
            producer.produceEvent("Message " + i);
        }

        // Then - Verify cumulative statistics
        TimingMetrics.TimingStat produceEventStats = producer.getTimingStats("produceEvent");
        assertNotNull(produceEventStats);
        assertEquals(numberOfCalls, produceEventStats.getCallCount());
        assertEquals(0, produceEventStats.getFailureCount());
        
        TimingMetrics.TimingStat onEventStats = listener.getTimingStats("onEvent");
        assertNotNull(onEventStats);
        assertEquals(numberOfCalls, onEventStats.getCallCount());
        assertEquals(0, onEventStats.getFailureCount());

        // Verify min/max timing statistics are reasonable
        assertTrue(onEventStats.getMinTimeMs() >= 0);
        assertTrue(onEventStats.getMaxTimeMs() >= onEventStats.getMinTimeMs());
        assertTrue(onEventStats.getAverageTimeMs() >= 0);
        assertTrue(onEventStats.getTotalTimeNanos() > 0);
    }

    @Test
    void shouldNotInterfereWithEventClearing() {
        // Given - Create avaje-inject context
        beanScope = BeanScope.builder().build();
        
        EventProducer producer = beanScope.get(EventProducer.class);
        EventListener listener = beanScope.get(EventListener.class);

        // When - Produce events, then clear
        producer.produceEvent("Message before clear");
        assertEquals(1, listener.getEventCount());
        
        listener.clearEvents();
        
        // Then - Verify clearing still works with interceptors
        assertEquals(0, listener.getEventCount());
        assertTrue(listener.getReceivedEvents().isEmpty());

        // Verify timing statistics for clearEvents are recorded
        TimingMetrics.TimingStat clearEventsStats = listener.getTimingStats("clearEvents");
        assertNotNull(clearEventsStats);
        assertEquals(1, clearEventsStats.getCallCount());
        assertEquals(0, clearEventsStats.getFailureCount());
    }

    @Test
    void shouldProvideTimingStatisticsLogging() {
        // Given - Create avaje-inject context
        beanScope = BeanScope.builder().build();
        
        EventProducer producer = beanScope.get(EventProducer.class);
        EventListener listener = beanScope.get(EventListener.class);

        // When - Use the system to generate some statistics
        producer.produceEvent("Test message");
        listener.getEventCount();

        // Then - Verify logging methods don't throw exceptions
        assertDoesNotThrow(() -> producer.logTimingStats());
        assertDoesNotThrow(() -> listener.logTimingStats());
    }
}