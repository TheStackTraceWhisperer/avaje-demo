package com.example.demo;

/**
 * Simple event class that carries a message.
 */
public class DemoEvent {
    private final String message;
    private final long timestamp;

    public DemoEvent(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "DemoEvent{message='" + message + "', timestamp=" + timestamp + "}";
    }
}