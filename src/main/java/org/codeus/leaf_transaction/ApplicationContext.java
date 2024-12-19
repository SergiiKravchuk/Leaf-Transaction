package org.codeus.leaf_transaction;

import org.codeus.leaf_transaction.annotations.Service;
import org.codeus.leaf_transaction.annotations.Transactional;
import org.codeus.leaf_transaction.dao.ParticipantDao;
import org.codeus.leaf_transaction.dao.SimpleParticipantDao;
import org.codeus.leaf_transaction.datasource.SingleConnectionDataSource;
import org.codeus.leaf_transaction.exception.BeanNotFoundException;
import org.codeus.leaf_transaction.reflection.TransactionalProxyInvocationHandler;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A Leaf representation of the Spring's IoC container (ApplicationContext) that instantiate and customizes Beans
 * Provides a limited set of methods.
 */
public class ApplicationContext {

  private final SingleConnectionDataSource dataSource;
  private final ParticipantDao participantDao;
  protected List<BeanContainer> beanContainers;

  public ApplicationContext(SingleConnectionDataSource dataSource) {
    this(dataSource, new SimpleParticipantDao(dataSource));
  }

  public ApplicationContext(SingleConnectionDataSource dataSource, ParticipantDao participantDao) {
    this.dataSource = dataSource;
    this.participantDao = participantDao;

    Reflections reflections = new Reflections("org.codeus.leaf_transaction");
    Set<Class<?>> beanClasses = reflections.getTypesAnnotatedWith(Service.class);
    List<?> rawBeans = instantiateBeans(beanClasses);
    this.beanContainers = postProcessBeans(rawBeans);
  }

  public List<?> instantiateBeans(Set<Class<?>> beanClasses) {
    return beanClasses.stream()
      .map(this::instantiateBean)
      .toList();
  }

  /**
   * Instantiates a given class  using the default, no-arg constructor
   *
   * @param beanClass class to be instantiated
   * @return an instance of beanClass
   */
  private Object instantiateBean(Class<?> beanClass) {
    try {
      Optional<Constructor<?>> daoDependentConstructor = getDaoDependentConstructor(beanClass);

      if (daoDependentConstructor.isPresent()) return daoDependentConstructor.get().newInstance(participantDao);
      else return beanClass.getConstructor().newInstance();
    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Could not instantiate bean by class=%s".formatted(beanClass), e);
    }
  }

  /**
   * Searches for a public constructor with a single parameter with type {@link ParticipantDao}.
   */
  private Optional<Constructor<?>> getDaoDependentConstructor(Class<?> beanClass) {
    return Arrays.stream(beanClass.getConstructors())
      .filter(constructor -> constructor.getParameterTypes().length == 1)
      .filter(constructor -> Arrays.stream(constructor.getParameterTypes()).anyMatch(ParticipantDao.class::isAssignableFrom))
      .findFirst();
  }

  private List<BeanContainer> postProcessBeans(List<?> beans) {
    return beans.stream()
      .map(this::postProcessBean)
      .toList();
  }

  private BeanContainer postProcessBean(Object bean) {
    Class<?> beanClass = bean.getClass();
    Class<?>[] beanClassInterfaces = beanClass.getInterfaces();

    Object customizedBean = bean;
    if (hasTransactionalMethods(beanClass)) {
      customizedBean = Proxy.newProxyInstance(
        beanClass.getClassLoader(),
        beanClassInterfaces,
        new TransactionalProxyInvocationHandler(bean, beanClass, dataSource)
      );
    }

    return new BeanContainer(customizedBean, beanClassInterfaces);
  }

  /**
   * Searches and returns a Bean instance for the given class.
   * <p>If instance cannot be found for the given beanClass, it throws {@link BeanNotFoundException}.
   * <p></p>Bean stored as a singleton objects, meaning that multiple calls of this method with the same class return the same object.
   *
   * @param beanClass type the bean must match, can be only an interface.
   * @return an instance of the single bean matching the required type.
   */
  public <T> T getBean(Class<T> beanClass) {
    Object bean = beanContainers.stream()
      .filter(p -> p.hasInterface(beanClass))
      .findFirst()
      .map(BeanContainer::getBean)
      .orElseThrow(() -> new BeanNotFoundException(beanClass));
    return (T) bean;
  }

  /**
   * Check whether a given beanClass has at least one method that is annotated with {@link Transactional} annotation.
   *
   * @param beanClass class whose methods to check.
   * @return true if beanClass has at least one method annotated with {@link Transactional} annotation, false - otherwise.
   */
  private <T> boolean hasTransactionalMethods(Class<T> beanClass) {
    for (Method method : beanClass.getDeclaredMethods())
      if (method.isAnnotationPresent(Transactional.class)) return true;
    return false;
  }
}
