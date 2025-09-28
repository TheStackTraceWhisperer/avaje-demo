package com.example.demo;

import com.example.demo.annotation.TraceLogged;
import com.example.demo.annotation.Timed;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Event producer that creates and sends events to the listener.
 * Demonstrates dependency injection with avaje-inject.
 * Enhanced with aspect-oriented trace logging and timing metrics.
 */
@Singleton
@TraceLogged
@Timed
public class EventProducer {
    private final EventListener eventListener;

    /**
     * Constructor injection - avaje-inject will provide the EventListener.
     */
    @Inject
    public EventProducer(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Produce and send an event.
     */
    public void produceEvent(String message) {
        DemoEvent event = new DemoEvent(message);
        eventListener.onEvent(event);
    }

    /**
     * Produce multiple events.
     */
    public void produceEvents(String... messages) {
        for (String message : messages) {
            produceEvent(message);
        }
    }
}