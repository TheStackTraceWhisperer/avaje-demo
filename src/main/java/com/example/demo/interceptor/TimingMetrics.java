package com.example.demo.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Simple timing utility that provides method execution time measurement.
 * This class tracks timing statistics for methods and provides simple metrics.
 */
public class TimingMetrics {
    
    private static final Logger logger = LoggerFactory.getLogger(TimingMetrics.class);
    
    // Thread-safe map to store timing statistics
    private final ConcurrentHashMap<String, TimingStat> timingStats = new ConcurrentHashMap<>();
    private final Class<?> ownerClass;
    
    public TimingMetrics(Class<?> ownerClass) {
        this.ownerClass = ownerClass;
    }
    
    /**
     * Execute a method with timing measurement.
     */
    public <T> T timeMethod(String methodName, ThrowableSupplier<T> supplier) throws Exception {
        String metricName = ownerClass.getSimpleName() + "." + methodName;
        long startTime = System.nanoTime();
        
        try {
            T result = supplier.get();
            recordTiming(metricName, startTime);
            return result;
        } catch (Exception e) {
            recordTiming(metricName, startTime, e);
            throw e;
        }
    }
    
    /**
     * Execute a void method with timing measurement.
     */
    public void timeVoidMethod(String methodName, ThrowableRunnable runnable) throws Exception {
        String metricName = ownerClass.getSimpleName() + "." + methodName;
        long startTime = System.nanoTime();
        
        try {
            runnable.run();
            recordTiming(metricName, startTime);
        } catch (Exception e) {
            recordTiming(metricName, startTime, e);
            throw e;
        }
    }
    
    /**
     * Record successful timing.
     */
    private void recordTiming(String metricName, long startTimeNanos) {
        long executionTimeNanos = System.nanoTime() - startTimeNanos;
        double executionTimeMs = executionTimeNanos / 1_000_000.0;
        
        TimingStat stat = timingStats.computeIfAbsent(metricName, k -> new TimingStat());
        stat.recordExecution(executionTimeNanos);
        
        if (logger.isDebugEnabled()) {
            logger.debug("TIMED: {} executed in {:.3f} ms (total calls: {}, avg: {:.3f} ms)", 
                        metricName, executionTimeMs, stat.getCallCount(), stat.getAverageTimeMs());
        }
    }
    
    /**
     * Record timing with exception.
     */
    private void recordTiming(String metricName, long startTimeNanos, Exception e) {
        long executionTimeNanos = System.nanoTime() - startTimeNanos;
        double executionTimeMs = executionTimeNanos / 1_000_000.0;
        
        TimingStat stat = timingStats.computeIfAbsent(metricName, k -> new TimingStat());
        stat.recordFailedExecution(executionTimeNanos);
        
        logger.warn("TIMED: {} failed after {:.3f} ms with exception: {}", 
                   metricName, executionTimeMs, e.getMessage());
    }
    
    /**
     * Get timing statistics for a method.
     */
    public TimingStat getTimingStats(String methodName) {
        String metricName = ownerClass.getSimpleName() + "." + methodName;
        return timingStats.get(metricName);
    }
    
    /**
     * Get all timing statistics.
     */
    public ConcurrentHashMap<String, TimingStat> getAllTimingStats() {
        return new ConcurrentHashMap<>(timingStats);
    }
    
    /**
     * Log all timing statistics.
     */
    public void logAllStats() {
        if (timingStats.isEmpty()) {
            logger.info("No timing statistics available for {}", ownerClass.getSimpleName());
            return;
        }
        
        logger.info("Timing statistics for {}:", ownerClass.getSimpleName());
        timingStats.forEach((method, stat) -> 
            logger.info("  {}: {} calls, avg: {:.3f} ms, min: {:.3f} ms, max: {:.3f} ms, failures: {}", 
                       method, stat.getCallCount(), stat.getAverageTimeMs(), 
                       stat.getMinTimeMs(), stat.getMaxTimeMs(), stat.getFailureCount())
        );
    }
    
    /**
     * Thread-safe timing statistics holder.
     */
    public static class TimingStat {
        private final LongAdder callCount = new LongAdder();
        private final LongAdder failureCount = new LongAdder();
        private final LongAdder totalTimeNanos = new LongAdder();
        private volatile long minTimeNanos = Long.MAX_VALUE;
        private volatile long maxTimeNanos = Long.MIN_VALUE;
        
        public void recordExecution(long executionTimeNanos) {
            callCount.increment();
            totalTimeNanos.add(executionTimeNanos);
            updateMinMax(executionTimeNanos);
        }
        
        public void recordFailedExecution(long executionTimeNanos) {
            callCount.increment();
            failureCount.increment();
            totalTimeNanos.add(executionTimeNanos);
            updateMinMax(executionTimeNanos);
        }
        
        private synchronized void updateMinMax(long executionTimeNanos) {
            if (executionTimeNanos < minTimeNanos) {
                minTimeNanos = executionTimeNanos;
            }
            if (executionTimeNanos > maxTimeNanos) {
                maxTimeNanos = executionTimeNanos;
            }
        }
        
        public long getCallCount() {
            return callCount.sum();
        }
        
        public long getFailureCount() {
            return failureCount.sum();
        }
        
        public double getAverageTimeMs() {
            long count = callCount.sum();
            return count > 0 ? (totalTimeNanos.sum() / 1_000_000.0) / count : 0.0;
        }
        
        public double getMinTimeMs() {
            return minTimeNanos == Long.MAX_VALUE ? 0.0 : minTimeNanos / 1_000_000.0;
        }
        
        public double getMaxTimeMs() {
            return maxTimeNanos == Long.MIN_VALUE ? 0.0 : maxTimeNanos / 1_000_000.0;
        }
        
        public long getTotalTimeNanos() {
            return totalTimeNanos.sum();
        }
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