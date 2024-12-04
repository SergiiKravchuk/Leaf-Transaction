package org.codeus.leaf_transaction.exception;

public class BeanNotFoundException extends RuntimeException {
  public BeanNotFoundException(Class<?> clazz) {
    super("Bean for class = %s does not exist".formatted(clazz.getName()));
  }
}
