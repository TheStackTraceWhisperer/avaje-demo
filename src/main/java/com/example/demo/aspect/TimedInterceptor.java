package com.example.demo.aspect;

import io.avaje.inject.aop.MethodInterceptor;
import io.avaje.inject.aop.Invocation;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Aspect-oriented timing metrics interceptor using proper avaje AOP.
 * This interceptor measures execution time and records metrics for methods annotated with @Timed.
 */
public class TimedInterceptor implements MethodInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(TimedInterceptor.class);
    
    // Thread-safe map to store timing statistics
    private final ConcurrentHashMap<String, TimingStat> timingStats = new ConcurrentHashMap<>();
    
    @Override
    public void invoke(Invocation invocation) throws Throwable {
        String methodName = invocation.method().getName();
        String className = invocation.method().getDeclaringClass().getSimpleName();
        String metricName = className + "." + methodName;
        
        if (logger.isDebugEnabled()) {
            logger.debug("TIMED: Starting timer for {}", metricName);
        }
        
        long startTime = System.nanoTime();
        try {
            // Proceed with the actual method invocation
            invocation.invoke();
            
            // Record successful execution
            recordTiming(metricName, startTime, false);
            
        } catch (Throwable throwable) {
            // Record failed execution
            recordTiming(metricName, startTime, true);
            throw throwable;
        }
    }
    
    private void recordTiming(String metricName, long startTimeNanos, boolean failed) {
        long executionTimeNanos = System.nanoTime() - startTimeNanos;
        double executionTimeMs = executionTimeNanos / 1_000_000.0;
        
        TimingStat stat = timingStats.computeIfAbsent(metricName, k -> new TimingStat());
        if (failed) {
            stat.recordFailedExecution(executionTimeNanos);
        } else {
            stat.recordExecution(executionTimeNanos);
        }
        
        if (logger.isDebugEnabled()) {
            if (failed) {
                logger.warn("TIMED: {} failed after {:.3f} ms", metricName, executionTimeMs);
            } else {
                logger.debug("TIMED: {} executed in {:.3f} ms (total calls: {}, avg: {:.3f} ms)", 
                            metricName, executionTimeMs, stat.getCallCount(), stat.getAverageTimeMs());
            }
        }
    }
    
    /**
     * Get timing statistics for all methods.
     */
    public ConcurrentHashMap<String, TimingStat> getAllTimingStats() {
        return new ConcurrentHashMap<>(timingStats);
    }
    
    /**
     * Log all timing statistics.
     */
    public void logAllStats() {
        if (timingStats.isEmpty()) {
            logger.info("No timing statistics available");
            return;
        }
        
        logger.info("=== TIMING STATISTICS ===");
        timingStats.forEach((method, stat) -> 
            logger.info("{}: {} calls, avg: {:.3f} ms, min: {:.3f} ms, max: {:.3f} ms, failures: {}", 
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
}