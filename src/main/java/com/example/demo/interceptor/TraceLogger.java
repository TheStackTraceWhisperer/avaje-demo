package com.example.demo.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple trace logging utility that provides aspect-oriented logging capabilities.
 * This class provides methods to log method entry, exit, and execution time.
 */
public class TraceLogger {
    
    private final Logger logger;
    
    public TraceLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }
    
    /**
     * Log method entry with arguments.
     */
    public void logEntry(String methodName, Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug("TRACE: Entering {}() with args: {}", methodName, formatArgs(args));
        }
    }
    
    /**
     * Log method exit with execution time.
     */
    public void logExit(String methodName, long startTimeNanos) {
        if (logger.isDebugEnabled()) {
            long executionTime = System.nanoTime() - startTimeNanos;
            logger.debug("TRACE: Exiting {}() successfully in {} ns", methodName, executionTime);
        }
    }
    
    /**
     * Log method exit with exception and execution time.
     */
    public void logExitWithException(String methodName, long startTimeNanos, Throwable throwable) {
        long executionTime = System.nanoTime() - startTimeNanos;
        logger.warn("TRACE: Exiting {}() with exception after {} ns: {}", 
                   methodName, executionTime, throwable.getMessage());
    }
    
    /**
     * Execute a method with trace logging.
     */
    public <T> T traceMethod(String methodName, ThrowableSupplier<T> supplier, Object... args) throws Exception {
        logEntry(methodName, args);
        long startTime = System.nanoTime();
        
        try {
            T result = supplier.get();
            logExit(methodName, startTime);
            return result;
        } catch (Exception e) {
            logExitWithException(methodName, startTime, e);
            throw e;
        }
    }
    
    /**
     * Execute a void method with trace logging.
     */
    public void traceVoidMethod(String methodName, ThrowableRunnable runnable, Object... args) throws Exception {
        logEntry(methodName, args);
        long startTime = System.nanoTime();
        
        try {
            runnable.run();
            logExit(methodName, startTime);
        } catch (Exception e) {
            logExitWithException(methodName, startTime, e);
            throw e;
        }
    }
    
    /**
     * Format method arguments for logging.
     */
    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Object arg = args[i];
            if (arg == null) {
                sb.append("null");
            } else if (arg instanceof String) {
                sb.append("\"").append(arg).append("\"");
            } else {
                sb.append(arg.toString());
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Functional interface for methods that can throw exceptions and return a value.
     */
    @FunctionalInterface
    public interface ThrowableSupplier<T> {
        T get() throws Exception;
    }
    
    /**
     * Functional interface for methods that can throw exceptions and return void.
     */
    @FunctionalInterface
    public interface ThrowableRunnable {
        void run() throws Exception;
    }
}