package com.example.demo;

import com.example.demo.interceptor.TraceLogger;
import com.example.demo.interceptor.TimingMetrics;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Event listener that handles demo events.
 * Uses @Singleton to be managed by avaje-inject.
 * Enhanced with trace logging and timing metrics.
 */
@Singleton
public class EventListener {
    private final List<DemoEvent> receivedEvents = new ArrayList<>();
    private final TraceLogger traceLogger = new TraceLogger(EventListener.class);
    private final TimingMetrics timingMetrics = new TimingMetrics(EventListener.class);

    /**
     * Handle an incoming event with trace logging and timing.
     */
    public void onEvent(DemoEvent event) {
        try {
            timingMetrics.timeVoidMethod("onEvent", () -> {
                traceLogger.traceVoidMethod("onEvent", () -> {
                    System.out.println("Received event: " + event);
                    receivedEvents.add(event);
                }, event);
            });
        } catch (Exception e) {
            // Convert checked exception to runtime exception for simplicity
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all received events (useful for testing).
     */
    public List<DemoEvent> getReceivedEvents() {
        try {
            return timingMetrics.timeMethod("getReceivedEvents", () -> 
                traceLogger.traceMethod("getReceivedEvents", () -> new ArrayList<>(receivedEvents))
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Clear all received events (useful for testing).
     */
    public void clearEvents() {
        try {
            timingMetrics.timeVoidMethod("clearEvents", () -> {
                traceLogger.traceVoidMethod("clearEvents", () -> {
                    receivedEvents.clear();
                });
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get count of received events.
     */
    public int getEventCount() {
        try {
            return timingMetrics.timeMethod("getEventCount", () -> 
                traceLogger.traceMethod("getEventCount", () -> receivedEvents.size())
            );
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