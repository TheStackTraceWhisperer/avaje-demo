package com.example.demo.aspect;

import com.example.demo.annotation.TraceLogged;
import com.example.demo.annotation.Timed;
import io.avaje.inject.BeanScope;
import io.avaje.inject.test.InjectTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for aspect functionality.
 * This test verifies that the avaje AOP aspects are properly configured.
 */
@InjectTest
class AspectIntegrationTest {

    private BeanScope beanScope;

    @AfterEach
    void cleanup() {
        if (beanScope != null) {
            beanScope.close();
        }
    }

    @Test
    void shouldInjectAspectProviders() {
        // Given - Create avaje-inject context
        beanScope = BeanScope.builder().build();

        // When - Get aspect providers
        TraceLoggedProvider traceProvider = beanScope.get(TraceLoggedProvider.class);
        TimedProvider timedProvider = beanScope.get(TimedProvider.class);

        // Then - Verify providers are injected
        assertNotNull(traceProvider, "TraceLoggedProvider should be injected");
        assertNotNull(timedProvider, "TimedProvider should be injected");
        assertNotNull(timedProvider.getInterceptor(), "TimedInterceptor should be available");
    }

    @Test
    void shouldProvideInterceptorsForAnnotations() throws NoSuchMethodException {
        // Given - Create avaje-inject context
        beanScope = BeanScope.builder().build();
        
        TraceLoggedProvider traceProvider = beanScope.get(TraceLoggedProvider.class);
        TimedProvider timedProvider = beanScope.get(TimedProvider.class);

        // Mock method and annotations for testing
        TraceLogged traceLogged = TestClass.class.getAnnotation(TraceLogged.class);
        Timed timed = TestClass.class.getAnnotation(Timed.class);
        var method = TestClass.class.getMethod("testMethod");

        // When - Get interceptors from providers
        var traceInterceptor = traceProvider.interceptor(method, traceLogged);
        var timedInterceptor = timedProvider.interceptor(method, timed);

        // Then - Verify interceptors are provided
        assertNotNull(traceInterceptor, "TraceLoggingInterceptor should be provided");
        assertNotNull(timedInterceptor, "TimedInterceptor should be provided");
        assertInstanceOf(TraceLoggingInterceptor.class, traceInterceptor);
        assertInstanceOf(TimedInterceptor.class, timedInterceptor);
    }

    @TraceLogged
    @Timed
    public static class TestClass {
        public void testMethod() {
            // Test method for aspect testing
        }
    }
}