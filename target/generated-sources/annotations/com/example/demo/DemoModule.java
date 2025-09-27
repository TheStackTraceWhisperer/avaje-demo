package com.example.demo;

import io.avaje.inject.BeanScope;
import io.avaje.inject.InjectModule;
import io.avaje.inject.spi.AvajeModule;
import io.avaje.inject.spi.Builder;
import io.avaje.inject.spi.DependencyMeta;
import io.avaje.inject.spi.Generated;
import io.avaje.inject.spi.GenericType;
import java.lang.reflect.Type;

/**
 * Avaje Inject module for Demo.
 * 
 * When using the Java module system, this generated class should be explicitly
 * registered in module-info via a <code>provides</code> clause like:
 * 
 * <pre>{@code
 * 
 *   module example {
 *     requires io.avaje.inject;
 *     
 *     provides io.avaje.inject.spi.InjectExtension with com.example.demo.DemoModule;
 *     
 *   }
 * 
 * }</pre>
 */
@Generated("io.avaje.inject.generator")
@InjectModule()
public final class DemoModule implements AvajeModule {

  private Builder builder;

  @Override
  public Type[] autoProvides() {
    return new Type[] {
      com.example.demo.EventListener.class,
      com.example.demo.EventProducer.class,
    };
  }

  @Override
  public Class<?>[] classes() {
    return new Class<?>[] {
      com.example.demo.EventListener.class,
      com.example.demo.EventProducer.class,
    };
  }

  /**
   * Creates all the beans in order based on constructor dependencies.
   * The beans are registered into the builder along with callbacks for
   * field/method injection, and lifecycle support.
   */
  @Override
  public void build(Builder builder) {
    this.builder = builder;
    // create beans in order based on constructor dependencies
    // i.e. "provides" followed by "dependsOn"
    build_demo_EventListener();
    build_demo_EventProducer();
  }

  @DependencyMeta(
      type = "com.example.demo.EventListener",
      autoProvides = {"com.example.demo.EventListener"})
  private void build_demo_EventListener() {
    EventListener$DI.build(builder);
  }

  @DependencyMeta(
      type = "com.example.demo.EventProducer",
      dependsOn = {"com.example.demo.EventListener"},
      autoProvides = {"com.example.demo.EventProducer"})
  private void build_demo_EventProducer() {
    EventProducer$DI.build(builder);
  }

}
