# avaje-demo

A demonstration project showing how to correctly setup **avaje-inject** with JUnit tests, including an event listener and event producer pattern. **Enhanced with aspect-oriented trace logging and timing metrics.**

## Overview

This project demonstrates best practices for using avaje-inject dependency injection framework, featuring:

- ✅ **Proper dependency injection setup** with avaje-inject
- ✅ **Event-driven architecture** with producer/consumer pattern  
- ✅ **Comprehensive testing patterns** (unit and integration tests)
- ✅ **Aspect-oriented trace logging** for method execution tracking
- ✅ **Performance timing metrics** for method execution measurement
- ✅ **Clean separation of concerns** with interceptor pattern

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

### 3. Aspect-Oriented Programming (NEW!)
- **TraceLogger**: Utility for aspect-oriented method tracing
- **TimingMetrics**: Performance measurement and statistics collection
- **Method Interception**: Automatic logging of method entry/exit and execution time
- **Exception Handling**: Proper error tracking in interceptors

### 4. Testing Patterns

#### Unit Testing (`EventListenerTest`, `TraceLoggerTest`, `TimingMetricsTest`)
- Tests components in isolation without dependency injection
- Direct instantiation and method calls
- Fast execution, focused on single component behavior
- Comprehensive coverage of interceptor functionality

#### Component Testing (`EventSystemIntegrationTest`)
- Uses `@InjectTest` to enable avaje-inject testing framework
- Creates `BeanScope` to test full dependency injection context
- Tests component interactions and wiring
- Properly cleans up resources with `@AfterEach`

#### Integration Testing (`InterceptorIntegrationTest`)
- Tests aspect-oriented features with dependency injection
- Validates trace logging and timing metrics in real scenarios
- Ensures interceptors don't break existing functionality

## Running the Project

### Build and Test
```bash
mvn clean compile test
```

### Run the Demo
```bash
mvn exec:java -Dexec.mainClass="com.example.demo.DemoApplication"
```

Expected output (with debug logging enabled):
```
Starting Avaje Inject Demo with Interceptors...
Components successfully injected!

Producing some events with interceptor logging...
02:02:18.464 DEBUG EventProducer - TRACE: Entering produceEvent() with args: ["Hello from avaje-inject with interceptors!"]
02:02:18.471 DEBUG EventListener - TRACE: Entering onEvent() with args: [DemoEvent{message='Hello from avaje-inject with interceptors!', timestamp=...}]
Received event: DemoEvent{message='Hello from avaje-inject with interceptors!', timestamp=...}
02:02:18.472 DEBUG EventListener - TRACE: Exiting onEvent() successfully in 205296 ns
02:02:18.473 DEBUG TimingMetrics - TIMED: EventListener.onEvent executed in 5.206 ms (total calls: 1, avg: 5.206 ms)
...

=== TIMING STATISTICS ===
Timing statistics for EventProducer:
  EventProducer.produceEvent: 5 calls, avg: 2.751 ms, min: 0.893 ms, max: 10.049 ms, failures: 0
  EventProducer.produceEvents: 1 calls, avg: 3.790 ms, min: 3.790 ms, max: 3.790 ms, failures: 0
Timing statistics for EventListener:
  EventListener.onEvent: 5 calls, avg: 1.413 ms, min: 0.402 ms, max: 5.206 ms, failures: 0
  EventListener.getEventCount: 1 calls, avg: 0.485 ms, min: 0.485 ms, max: 0.485 ms, failures: 0
  EventListener.getReceivedEvents: 1 calls, avg: 0.446 ms, min: 0.446 ms, max: 0.446 ms, failures: 0

Demo completed!
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
- `slf4j-simple` - Logging implementation for trace logging
- `junit-jupiter` - JUnit 5 for tests

### 4. Aspect-Oriented Programming Features

#### TraceLogger
```java
@Singleton
public class EventProducer {
    private final TraceLogger traceLogger = new TraceLogger(EventProducer.class);
    
    public void produceEvent(String message) {
        traceLogger.traceVoidMethod("produceEvent", () -> {
            // Method implementation
        }, message);
    }
}
```

#### TimingMetrics
```java
@Singleton
public class EventListener {
    private final TimingMetrics timingMetrics = new TimingMetrics(EventListener.class);
    
    public int getEventCount() {
        return timingMetrics.timeMethod("getEventCount", () -> {
            return receivedEvents.size();
        });
    }
    
    public void logTimingStats() {
        timingMetrics.logAllStats(); // Log performance statistics
    }
}
```

#### Configuration for Debug Logging
Create `src/main/resources/simplelogger.properties`:
```properties
org.slf4j.simpleLogger.defaultLogLevel=debug
org.slf4j.simpleLogger.log.com.example.demo=debug
org.slf4j.simpleLogger.showDateTime=true
org.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss.SSS
```

## Interceptor Features

### TraceLogger Utility

The `TraceLogger` class provides aspect-oriented method tracing with the following features:

- **Method Entry/Exit Logging**: Automatically logs when methods are called and completed
- **Argument Logging**: Captures and logs method arguments (with null-safety)
- **Execution Time Tracking**: Measures and logs method execution time in nanoseconds
- **Exception Handling**: Logs exceptions with execution time before re-throwing
- **Flexible Integration**: Works with both void methods and methods returning values

### TimingMetrics Utility

The `TimingMetrics` class provides comprehensive performance measurement:

- **Thread-Safe Statistics**: Uses `ConcurrentHashMap` and `LongAdder` for thread safety
- **Call Count Tracking**: Records total number of method invocations
- **Execution Time Statistics**: Tracks min, max, average, and total execution times
- **Failure Tracking**: Separately tracks failed method executions
- **Statistical Reporting**: Provides detailed performance reports via logging

### Benefits

1. **Non-Invasive**: Interceptors don't modify business logic, just wrap it
2. **Configurable**: Debug logging can be enabled/disabled via configuration
3. **Performance Monitoring**: Real-time method performance statistics
4. **Debugging Support**: Detailed trace logs help with troubleshooting
5. **Production Ready**: Thread-safe implementation suitable for production use

## References

- [avaje-inject Documentation](https://avaje.io/inject/)
- [Unit Testing Guide](https://avaje.io/inject/#unit-testing)
- [Component Testing Guide](https://avaje.io/inject/#component-testing)
- [SLF4J Simple Logger Configuration](https://www.slf4j.org/apidocs/org/slf4j/simple/SimpleLogger.html)
