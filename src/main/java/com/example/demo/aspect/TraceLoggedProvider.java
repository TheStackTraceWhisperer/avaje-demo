package com.example.demo.aspect;

import io.avaje.inject.aop.AspectProvider;
import io.avaje.inject.aop.MethodInterceptor;
import com.example.demo.annotation.TraceLogged;
import jakarta.inject.Singleton;
import java.lang.reflect.Method;

/**
 * Aspect provider for trace logging functionality.
 */
@Singleton
public class TraceLoggedProvider implements AspectProvider<TraceLogged> {
    
    private final TraceLoggingInterceptor interceptor = new TraceLoggingInterceptor();
    
    @Override
    public MethodInterceptor interceptor(Method method, TraceLogged annotation) {
        return interceptor;
    }
}