package com.example.demo;

import io.avaje.inject.BeanScope;

/**
 * Main demonstration class showing how to use avaje-inject
 * to wire up the event system and run it.
 */
public class DemoApplication {

    public static void main(String[] args) {
        System.out.println("Starting Avaje Inject Demo...");

        // Create the bean scope - this is where avaje-inject does its magic
        try (BeanScope beanScope = BeanScope.builder().build()) {
            
            // Get our components - avaje-inject handles the wiring
            EventProducer producer = beanScope.get(EventProducer.class);
            EventListener listener = beanScope.get(EventListener.class);

            System.out.println("Components successfully injected!");
            
            // Demonstrate the event system
            System.out.println("\nProducing some events...");
            producer.produceEvent("Hello from avaje-inject!");
            producer.produceEvent("This demonstrates dependency injection");
            producer.produceEvents("Event 1", "Event 2", "Event 3");

            // Show the results
            System.out.println("\nResults:");
            System.out.println("Total events received: " + listener.getEventCount());
            
            System.out.println("\nAll events:");
            listener.getReceivedEvents().forEach(event -> 
                System.out.println("  - " + event)
            );

        } catch (Exception e) {
            System.err.println("Error running demo: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nDemo completed!");
    }
}