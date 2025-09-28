package com.example.demo;

import com.example.demo.annotation.TraceLogged;
import com.example.demo.annotation.Timed;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Event listener that handles demo events.
 * Uses @Singleton to be managed by avaje-inject.
 * Enhanced with aspect-oriented trace logging and timing metrics.
 */
@Singleton
@TraceLogged
@Timed
public class EventListener {
    private final List<DemoEvent> receivedEvents = new ArrayList<>();

    /**
     * Handle an incoming event.
     */
    public void onEvent(DemoEvent event) {
        System.out.println("Received event: " + event);
        receivedEvents.add(event);
    }

    /**
     * Get all received events (useful for testing).
     */
    public List<DemoEvent> getReceivedEvents() {
        return new ArrayList<>(receivedEvents);
    }

    /**
     * Clear all received events (useful for testing).
     */
    public void clearEvents() {
        receivedEvents.clear();
    }

    /**
     * Get count of received events.
     */
    public int getEventCount() {
        return receivedEvents.size();
    }
}