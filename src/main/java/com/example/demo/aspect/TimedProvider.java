package com.example.demo.aspect;

import io.avaje.inject.aop.AspectProvider;
import io.avaje.inject.aop.MethodInterceptor;
import com.example.demo.annotation.Timed;
import jakarta.inject.Singleton;
import java.lang.reflect.Method;

/**
 * Aspect provider for timing metrics functionality.
 */
@Singleton
public class TimedProvider implements AspectProvider<Timed> {
    
    private final TimedInterceptor interceptor = new TimedInterceptor();
    
    @Override
    public MethodInterceptor interceptor(Method method, Timed annotation) {
        return interceptor;
    }
    
    /**
     * Get access to the interceptor for statistics reporting.
     */
    public TimedInterceptor getInterceptor() {
        return interceptor;
    }
}