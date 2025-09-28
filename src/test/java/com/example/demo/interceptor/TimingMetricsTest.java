package com.example.demo.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TimingMetrics interceptor functionality.
 */
class TimingMetricsTest {

    private TimingMetrics timingMetrics;

    @BeforeEach
    void setUp() {
        timingMetrics = new TimingMetrics(TimingMetricsTest.class);
    }

    @Test
    void shouldTimeMethodWithReturnValue() throws Exception {
        // Given
        String expectedResult = "test result";
        String methodName = "testMethod";
        
        // When
        String actualResult = timingMetrics.timeMethod(methodName, () -> {
            Thread.sleep(10); // Simulate some work
            return expectedResult;
        });

        // Then
        assertEquals(expectedResult, actualResult);
        
        TimingMetrics.TimingStat stats = timingMetrics.getTimingStats(methodName);
        assertNotNull(stats);
        assertEquals(1, stats.getCallCount());
        assertEquals(0, stats.getFailureCount());
        assertTrue(stats.getAverageTimeMs() > 0);
    }

    @Test
    void shouldTimeVoidMethod() throws Exception {
        // Given
        boolean[] methodExecuted = {false};
        String methodName = "testVoidMethod";
        
        // When
        timingMetrics.timeVoidMethod(methodName, () -> {
            Thread.sleep(10); // Simulate some work
            methodExecuted[0] = true;
        });

        // Then
        assertTrue(methodExecuted[0]);
        
        TimingMetrics.TimingStat stats = timingMetrics.getTimingStats(methodName);
        assertNotNull(stats);
        assertEquals(1, stats.getCallCount());
        assertEquals(0, stats.getFailureCount());
        assertTrue(stats.getAverageTimeMs() > 0);
    }

    @Test
    void shouldRecordFailureForTimedMethodWithException() {
        // Given
        String methodName = "failingMethod";
        String errorMessage = "Test exception";
        
        // When/Then
        Exception thrownException = assertThrows(Exception.class, () -> {
            timingMetrics.timeMethod(methodName, () -> {
                Thread.sleep(5); // Simulate some work before failure
                throw new RuntimeException(errorMessage);
            });
        });

        assertEquals(errorMessage, thrownException.getMessage());
        
        TimingMetrics.TimingStat stats = timingMetrics.getTimingStats(methodName);
        assertNotNull(stats);
        assertEquals(1, stats.getCallCount());
        assertEquals(1, stats.getFailureCount());
        assertTrue(stats.getAverageTimeMs() > 0);
    }

    @Test
    void shouldRecordFailureForTimedVoidMethodWithException() {
        // Given
        String methodName = "failingVoidMethod";
        String errorMessage = "Test void exception";
        
        // When/Then
        Exception thrownException = assertThrows(Exception.class, () -> {
            timingMetrics.timeVoidMethod(methodName, () -> {
                Thread.sleep(5); // Simulate some work before failure
                throw new RuntimeException(errorMessage);
            });
        });

        assertEquals(errorMessage, thrownException.getMessage());
        
        TimingMetrics.TimingStat stats = timingMetrics.getTimingStats(methodName);
        assertNotNull(stats);
        assertEquals(1, stats.getCallCount());
        assertEquals(1, stats.getFailureCount());
        assertTrue(stats.getAverageTimeMs() > 0);
    }

    @Test
    void shouldAccumulateTimingStatistics() throws Exception {
        // Given
        String methodName = "accumulatedMethod";
        int numberOfCalls = 5;
        
        // When
        for (int i = 0; i < numberOfCalls; i++) {
            final int callIndex = i; // Make variable effectively final for lambda
            timingMetrics.timeMethod(methodName, () -> {
                Thread.sleep(2); // Consistent small delay
                return "result" + callIndex;
            });
        }

        // Then
        TimingMetrics.TimingStat stats = timingMetrics.getTimingStats(methodName);
        assertNotNull(stats);
        assertEquals(numberOfCalls, stats.getCallCount());
        assertEquals(0, stats.getFailureCount());
        assertTrue(stats.getAverageTimeMs() > 0);
        assertTrue(stats.getMinTimeMs() > 0);
        assertTrue(stats.getMaxTimeMs() >= stats.getMinTimeMs());
        assertTrue(stats.getTotalTimeNanos() > 0);
    }

    @Test
    void shouldHandleMixedSuccessAndFailureCalls() throws Exception {
        // Given
        String methodName = "mixedMethod";
        
        // When - Mix successful and failing calls
        // Successful call
        timingMetrics.timeMethod(methodName, () -> "success");
        
        // Failing call
        assertThrows(Exception.class, () -> {
            timingMetrics.timeMethod(methodName, () -> {
                throw new RuntimeException("failure");
            });
        });
        
        // Another successful call
        timingMetrics.timeMethod(methodName, () -> "success2");

        // Then
        TimingMetrics.TimingStat stats = timingMetrics.getTimingStats(methodName);
        assertNotNull(stats);
        assertEquals(3, stats.getCallCount());
        assertEquals(1, stats.getFailureCount());
        assertTrue(stats.getAverageTimeMs() >= 0);
    }

    @Test
    void shouldReturnNullForNonExistentMethod() {
        // Given
        String nonExistentMethod = "nonExistentMethod";
        
        // When
        TimingMetrics.TimingStat stats = timingMetrics.getTimingStats(nonExistentMethod);

        // Then
        assertNull(stats);
    }

    @Test
    void shouldProvideAllTimingStats() throws Exception {
        // Given
        timingMetrics.timeMethod("method1", () -> "result1");
        timingMetrics.timeVoidMethod("method2", () -> {});
        
        // When
        var allStats = timingMetrics.getAllTimingStats();

        // Then
        assertEquals(2, allStats.size());
        assertTrue(allStats.containsKey("TimingMetricsTest.method1"));
        assertTrue(allStats.containsKey("TimingMetricsTest.method2"));
    }

    @Test
    void shouldLogStatsWithoutCrashing() throws Exception {
        // Given
        timingMetrics.timeMethod("testMethod", () -> "result");
        
        // When/Then - Should not throw any exceptions
        assertDoesNotThrow(() -> timingMetrics.logAllStats());
    }
}