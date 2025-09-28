package com.example.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable trace logging on methods or classes using avaje aspects.
 * When applied to a class, all public methods will be traced.
 * When applied to a method, only that specific method will be traced.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TraceLogged {
}