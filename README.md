# avaje-demo

A demonstration project showing how to correctly setup **avaje-inject** with JUnit tests, including an event listener and event producer pattern.

## Overview

This project demonstrates:
- ✅ Proper avaje-inject dependency injection setup
- ✅ Event-driven architecture with producer/listener pattern  
- ✅ Unit testing individual components
- ✅ Component testing with full dependency injection context
- ✅ Correct use of `@InjectTest` annotation
- ✅ Maven build configuration with avaje-inject

## Project Structure

```
src/
├── main/java/com/example/demo/
│   ├── DemoApplication.java      # Main application demonstrating the system
│   ├── DemoEvent.java           # Event data class
│   ├── EventListener.java       # @Singleton component that handles events
│   └── EventProducer.java       # @Singleton component that produces events
└── test/java/com/example/demo/
    ├── EventListenerTest.java           # Unit test for EventListener
    └── EventSystemIntegrationTest.java  # Component test with @InjectTest
```

## Key Features

### 1. Event System Architecture
- **DemoEvent**: Simple event class carrying a message and timestamp
- **EventListener**: Singleton component that receives and stores events
- **EventProducer**: Singleton component that creates events and sends them to the listener

### 2. Correct avaje-inject Setup
- Uses `@Singleton` for component registration
- Uses `@Inject` for constructor injection
- Proper Maven configuration with annotation processor
- Demonstrates dependency wiring between components

### 3. Testing Patterns

#### Unit Testing (`EventListenerTest`)
- Tests components in isolation without dependency injection
- Direct instantiation and method calls
- Fast execution, focused on single component behavior

#### Component Testing (`EventSystemIntegrationTest`)
- Uses `@InjectTest` to enable avaje-inject testing framework
- Creates `BeanScope` to test full dependency injection context
- Tests component interactions and wiring
- Properly cleans up resources with `@AfterEach`

## Running the Project

### Build and Test
```bash
mvn clean compile test
```

### Run the Demo
```bash
mvn exec:java -Dexec.mainClass="com.example.demo.DemoApplication"
```

Expected output:
```
Starting Avaje Inject Demo...
Components successfully injected!

Producing some events...
Received event: DemoEvent{message='Hello from avaje-inject!', timestamp=...}
...

Results:
Total events received: 5
```

## Key Learning Points

### 1. Correct Test Setup with avaje-inject

```java
@InjectTest  // Enable avaje-inject testing
class EventSystemIntegrationTest {
    
    private BeanScope beanScope;

    @AfterEach
    void cleanup() {
        if (beanScope != null) {
            beanScope.close();  // Important: clean up resources
        }
    }

    @Test
    void shouldHandleEventFlow() {
        // Create avaje-inject context
        beanScope = BeanScope.builder().build();
        
        // Get injected beans
        EventProducer producer = beanScope.get(EventProducer.class);
        EventListener listener = beanScope.get(EventListener.class);
        
        // Test the system...
    }
}
```

### 2. Component Registration

```java
@Singleton  // Register with avaje-inject
public class EventListener {
    // Component implementation
}

@Singleton
public class EventProducer {
    private final EventListener eventListener;

    @Inject  // Constructor injection
    public EventProducer(EventListener eventListener) {
        this.eventListener = eventListener;
    }
}
```

### 3. Maven Configuration

Key dependencies and configuration:
- `avaje-inject` - Core dependency injection
- `avaje-inject-generator` - Annotation processor (compile-time)  
- `avaje-inject-test` - Testing utilities
- `junit-jupiter` - JUnit 5 for tests

## References

- [avaje-inject Documentation](https://avaje.io/inject/)
- [Unit Testing Guide](https://avaje.io/inject/#unit-testing)
- [Component Testing Guide](https://avaje.io/inject/#component-testing)
