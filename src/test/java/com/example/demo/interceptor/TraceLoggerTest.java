package com.example.demo.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TraceLogger interceptor functionality.
 */
class TraceLoggerTest {

    private TraceLogger traceLogger;

    @BeforeEach
    void setUp() {
        traceLogger = new TraceLogger(TraceLoggerTest.class);
    }

    @Test
    void shouldTraceMethodWithReturnValue() throws Exception {
        // Given
        String expectedResult = "test result";
        
        // When
        String actualResult = traceLogger.traceMethod("testMethod", 
            () -> expectedResult, "arg1", "arg2");

        // Then
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void shouldTraceVoidMethod() throws Exception {
        // Given
        boolean[] methodExecuted = {false};
        
        // When
        traceLogger.traceVoidMethod("testVoidMethod", 
            () -> methodExecuted[0] = true, "arg1");

        // Then
        assertTrue(methodExecuted[0]);
    }

    @Test
    void shouldPropagateExceptionsFromTracedMethod() {
        // Given
        String errorMessage = "Test exception";
        
        // When/Then
        Exception thrownException = assertThrows(Exception.class, () -> {
            traceLogger.traceMethod("failingMethod", () -> {
                throw new RuntimeException(errorMessage);
            });
        });

        assertEquals(errorMessage, thrownException.getMessage());
    }

    @Test
    void shouldPropagateExceptionsFromTracedVoidMethod() {
        // Given
        String errorMessage = "Test void exception";
        
        // When/Then
        Exception thrownException = assertThrows(Exception.class, () -> {
            traceLogger.traceVoidMethod("failingVoidMethod", () -> {
                throw new RuntimeException(errorMessage);
            });
        });

        assertEquals(errorMessage, thrownException.getMessage());
    }

    @Test
    void shouldHandleNullArguments() throws Exception {
        // Given
        String expectedResult = "null args handled";
        
        // When
        String actualResult = traceLogger.traceMethod("methodWithNullArgs", 
            () -> expectedResult, (Object[]) null);

        // Then
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void shouldHandleEmptyArguments() throws Exception {
        // Given
        String expectedResult = "empty args handled";
        
        // When
        String actualResult = traceLogger.traceMethod("methodWithEmptyArgs", 
            () -> expectedResult);

        // Then
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void shouldHandleMixedArgumentTypes() throws Exception {
        // Given
        String expectedResult = "mixed args handled";
        Object[] mixedArgs = {null, "string", 42, true};
        
        // When
        String actualResult = traceLogger.traceMethod("methodWithMixedArgs", 
            () -> expectedResult, mixedArgs);

        // Then
        assertEquals(expectedResult, actualResult);
    }
}