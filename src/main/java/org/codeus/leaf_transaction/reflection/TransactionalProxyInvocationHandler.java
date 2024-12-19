package org.codeus.leaf_transaction.reflection;

import org.codeus.leaf_transaction.annotations.Transactional;
import org.codeus.leaf_transaction.datasource.SingleConnectionDataSource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Dynamic-Proxy that adds JDBC transaction control logic for methods annotated with {@link Transactional} annotation
 */
public class TransactionalProxyInvocationHandler implements InvocationHandler {
  private final Object target;
  private final Class<?> targetClass;

  private final SingleConnectionDataSource singleConnectionDataSource;

  public TransactionalProxyInvocationHandler(Object target, Class<?> targetClass, SingleConnectionDataSource singleConnectionDataSource) {
    this.target = target;
    this.targetClass = targetClass;
    this.singleConnectionDataSource = singleConnectionDataSource;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
    var targetClassMethod = getOverriddenMethod(method);

    if (targetClassMethod.isAnnotationPresent(Transactional.class)) {
      Connection connection = singleConnectionDataSource.getConnection();
      try {
        connection.setAutoCommit(false);
        System.out.println("Transaction started");
        Object result = method.invoke(target, args);
        connection.commit();
        System.out.println("Transaction committed");
        return result;
      } catch (Exception e) {
        connection.rollback();
        System.out.println("Transaction rolled back");
        throw e;
      }
    }
    return method.invoke(target, args);
  }

  /**
   * Returns the method from {@link TransactionalProxyInvocationHandler#target} with the same signature ( the
   * implementation of a method defined in interface)
   */
  private Method getOverriddenMethod(Method method) throws NoSuchMethodException {
    return targetClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
  }
}

