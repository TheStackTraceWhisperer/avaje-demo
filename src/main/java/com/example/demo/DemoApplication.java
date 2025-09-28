package com.example.demo;

import io.avaje.inject.BeanScope;

/**
 * Main demonstration class showing how to use avaje-inject
 * to wire up the event system and run it.
 * Enhanced to demonstrate trace logging and timing metrics.
 */
public class DemoApplication {

    public static void main(String[] args) {
        System.out.println("Starting Avaje Inject Demo with Interceptors...");

        // Create the bean scope - this is where avaje-inject does its magic
        try (BeanScope beanScope = BeanScope.builder().build()) {
            
            // Get our components - avaje-inject handles the wiring
            EventProducer producer = beanScope.get(EventProducer.class);
            EventListener listener = beanScope.get(EventListener.class);

            System.out.println("Components successfully injected!");
            
            // Demonstrate the event system with trace logging and timing
            System.out.println("\nProducing some events with interceptor logging...");
            producer.produceEvent("Hello from avaje-inject with interceptors!");
            producer.produceEvent("This demonstrates aspect-oriented programming");
            producer.produceEvents("Timed Event 1", "Timed Event 2", "Timed Event 3");

            // Show the results
            System.out.println("\nResults:");
            System.out.println("Total events received: " + listener.getEventCount());
            
            System.out.println("\nAll events:");
            listener.getReceivedEvents().forEach(event -> 
                System.out.println("  - " + event)
            );
            
            // Show timing statistics
            System.out.println("\n=== TIMING STATISTICS ===");
            producer.logTimingStats();
            listener.logTimingStats();

        } catch (Exception e) {
            System.err.println("Error running demo: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nDemo completed!");
    }
}