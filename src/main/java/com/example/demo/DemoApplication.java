package com.example.demo;

import com.example.demo.aspect.TimedProvider;
import io.avaje.inject.BeanScope;

/**
 * Main demonstration class showing how to use avaje-inject
 * to wire up the event system and run it.
 * Enhanced to demonstrate aspect-oriented trace logging and timing metrics.
 */
public class DemoApplication {

    public static void main(String[] args) {
        System.out.println("Starting Avaje Inject Demo with Aspect-Oriented Programming...");

        // Create the bean scope - this is where avaje-inject does its magic
        try (BeanScope beanScope = BeanScope.builder().build()) {
            
            // Get our components - avaje-inject handles the wiring
            EventProducer producer = beanScope.get(EventProducer.class);
            EventListener listener = beanScope.get(EventListener.class);
            TimedProvider timedProvider = beanScope.get(TimedProvider.class);

            System.out.println("Components successfully injected!");
            
            // Demonstrate the event system with AOP aspects
            System.out.println("\nProducing some events with aspect-oriented logging and timing...");
            producer.produceEvent("Hello from avaje aspects!");
            producer.produceEvent("This demonstrates proper aspect-oriented programming");
            producer.produceEvents("Timed Event 1", "Timed Event 2", "Timed Event 3");

            // Show the results
            System.out.println("\nResults:");
            System.out.println("Total events received: " + listener.getEventCount());
            
            System.out.println("\nAll events:");
            listener.getReceivedEvents().forEach(event -> 
                System.out.println("  - " + event)
            );
            
            // Show timing statistics from the aspect
            System.out.println();
            timedProvider.getInterceptor().logAllStats();

        } catch (Exception e) {
            System.err.println("Error running demo: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nDemo completed!");
    }
}