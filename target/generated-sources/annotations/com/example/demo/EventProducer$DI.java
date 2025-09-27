package com.example.demo;

import io.avaje.inject.spi.Builder;
import io.avaje.inject.spi.Generated;

/**
 * Generated source - dependency injection builder for EventProducer.
 */
@Generated("io.avaje.inject.generator")
public final class EventProducer$DI  {

  /**
   * Create and register EventProducer.
   */
  public static void build(Builder builder) {
    if (builder.isAddBeanFor(EventProducer.class)) {
      var bean = new EventProducer(builder.get(EventListener.class,"!eventListener"));
      builder.register(bean);
    }
  }

}
