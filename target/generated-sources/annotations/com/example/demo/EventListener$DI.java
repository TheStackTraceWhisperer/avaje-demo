package com.example.demo;

import io.avaje.inject.spi.Builder;
import io.avaje.inject.spi.Generated;

/**
 * Generated source - dependency injection builder for EventListener.
 */
@Generated("io.avaje.inject.generator")
public final class EventListener$DI  {

  /**
   * Create and register EventListener.
   */
  public static void build(Builder builder) {
    if (builder.isAddBeanFor(EventListener.class)) {
      var bean = new EventListener();
      builder.register(bean);
    }
  }

}
