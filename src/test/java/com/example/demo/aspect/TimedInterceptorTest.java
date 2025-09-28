package com.example.demo.aspect;

import io.avaje.inject.aop.Invocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the timing interceptor functionality.
 */
@ExtendWith(MockitoExtension.class)
class TimedInterceptorTest {

    @Mock
    private Invocation invocation;

    @Mock  
    private Method method;

    private TimedInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new TimedInterceptor();
    }

    @Test
    void shouldMeasureExecutionTime() throws Throwable {
        // Given
        when(invocation.method()).thenReturn(method);
        when(method.getName()).thenReturn("testMethod");
        when(method.getDeclaringClass()).thenReturn((Class) TimedInterceptorTest.class);

        // When
        interceptor.invoke(invocation);

        // Then
        verify(invocation).invoke();
        
        // Verify timing statistics were recorded
        var stats = interceptor.getAllTimingStats();
        assertTrue(stats.containsKey("TimedInterceptorTest.testMethod"));
        assertEquals(1, stats.get("TimedInterceptorTest.testMethod").getCallCount());
        assertEquals(0, stats.get("TimedInterceptorTest.testMethod").getFailureCount());
    }

    @Test
    void shouldRecordFailureWhenExceptionThrown() throws Throwable {
        // Given
        when(invocation.method()).thenReturn(method);
        when(method.getName()).thenReturn("failingMethod");
        when(method.getDeclaringClass()).thenReturn((Class) TimedInterceptorTest.class);
        doThrow(new RuntimeException("Test exception")).when(invocation).invoke();

        // When/Then
        assertThrows(RuntimeException.class, () -> interceptor.invoke(invocation));
        
        // Verify failure was recorded
        var stats = interceptor.getAllTimingStats();
        assertTrue(stats.containsKey("TimedInterceptorTest.failingMethod"));
        assertEquals(1, stats.get("TimedInterceptorTest.failingMethod").getCallCount());
        assertEquals(1, stats.get("TimedInterceptorTest.failingMethod").getFailureCount());
    }

    @Test
    void shouldAccumulateMultipleInvocations() throws Throwable {
        // Given
        when(invocation.method()).thenReturn(method);
        when(method.getName()).thenReturn("multipleCallsMethod");
        when(method.getDeclaringClass()).thenReturn((Class) TimedInterceptorTest.class);

        // When
        interceptor.invoke(invocation);
        interceptor.invoke(invocation);
        interceptor.invoke(invocation);

        // Then
        verify(invocation, times(3)).invoke();
        
        var stats = interceptor.getAllTimingStats();
        assertTrue(stats.containsKey("TimedInterceptorTest.multipleCallsMethod"));
        assertEquals(3, stats.get("TimedInterceptorTest.multipleCallsMethod").getCallCount());
        assertEquals(0, stats.get("TimedInterceptorTest.multipleCallsMethod").getFailureCount());
    }

    @Test
    void shouldLogStatsWithoutCrashing() {
        // Given - interceptor with some statistics
        TimedInterceptor interceptor = new TimedInterceptor();

        // When/Then - should not throw
        assertDoesNotThrow(() -> interceptor.logAllStats());
    }
}