package com.example.demo;

import com.example.demo.interceptor.TraceLogger;
import com.example.demo.interceptor.TimingMetrics;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Event producer that creates and sends events to the listener.
 * Demonstrates dependency injection with avaje-inject.
 * Enhanced with trace logging and timing metrics.
 */
@Singleton
public class EventProducer {
    private final EventListener eventListener;
    private final TraceLogger traceLogger = new TraceLogger(EventProducer.class);
    private final TimingMetrics timingMetrics = new TimingMetrics(EventProducer.class);

    /**
     * Constructor injection - avaje-inject will provide the EventListener.
     */
    @Inject
    public EventProducer(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Produce and send an event with trace logging and timing.
     */
    public void produceEvent(String message) {
        try {
            timingMetrics.timeVoidMethod("produceEvent", () -> {
                traceLogger.traceVoidMethod("produceEvent", () -> {
                    DemoEvent event = new DemoEvent(message);
                    eventListener.onEvent(event);
                }, message);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Produce multiple events with trace logging and timing.
     */
    public void produceEvents(String... messages) {
        try {
            timingMetrics.timeVoidMethod("produceEvents", () -> {
                traceLogger.traceVoidMethod("produceEvents", () -> {
                    for (String message : messages) {
                        produceEvent(message);
                    }
                }, (Object[]) messages);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Get timing statistics for this component.
     */
    public TimingMetrics.TimingStat getTimingStats(String methodName) {
        return timingMetrics.getTimingStats(methodName);
    }
    
    /**
     * Log all timing statistics.
     */
    public void logTimingStats() {
        timingMetrics.logAllStats();
    }
}