package com.example.demo.aspect;

import io.avaje.inject.aop.MethodInterceptor;
import io.avaje.inject.aop.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect-oriented trace logging interceptor using proper avaje AOP.
 * This interceptor logs method entry, exit, and execution time.
 */
public class TraceLoggingInterceptor implements MethodInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(TraceLoggingInterceptor.class);
    
    @Override
    public void invoke(Invocation invocation) throws Throwable {
        String methodName = invocation.method().getName();
        String className = invocation.method().getDeclaringClass().getSimpleName();
        Object[] args = invocation.arguments();
        
        // Log method entry
        if (logger.isDebugEnabled()) {
            logger.debug("TRACE: Entering {}.{}() with args: {}", className, methodName, formatArgs(args));
        }
        
        long startTime = System.nanoTime();
        try {
            // Proceed with the actual method invocation
            invocation.invoke();
            
            long executionTime = System.nanoTime() - startTime;
            
            // Log successful method exit
            if (logger.isDebugEnabled()) {
                logger.debug("TRACE: Exiting {}.{}() successfully in {} ns", className, methodName, executionTime);
            }
            
        } catch (Throwable throwable) {
            long executionTime = System.nanoTime() - startTime;
            
            // Log method exit with exception
            logger.warn("TRACE: Exiting {}.{}() with exception after {} ns: {}", 
                       className, methodName, executionTime, throwable.getMessage());
            
            // Re-throw the exception
            throw throwable;
        }
    }
    
    /**
     * Format method arguments for logging, handling null and array cases.
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
}