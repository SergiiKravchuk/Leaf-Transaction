package org.codeus.leaf_transaction;

import java.util.Arrays;
import java.util.List;

/**
 * A simple container for a bean instance, and it's original class' interfaces.
 */
public class BeanContainer {

  private final Object bean;
  private final List<Class<?>> interfaces;

  public BeanContainer(Object bean, Class<?>[] interfaces) {
    this.bean = bean;
    this.interfaces = Arrays.stream(interfaces).toList();
  }

  public Object getBean() {
    return bean;
  }

  /**
   * Verifies if a given parentInterface is an actual parent interface for this container's bean instance.
   * @param parentInterface interface to check if it is the actual parent interface
   * @return true if parentInterface is actual parent interface for this bean instance, false - otherwise.
   */
  public boolean hasInterface(Class<?> parentInterface) {
    return interfaces.contains(parentInterface);
  }
}
