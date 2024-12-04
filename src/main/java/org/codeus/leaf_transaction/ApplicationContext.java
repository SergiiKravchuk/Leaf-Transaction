package org.codeus.leaf_transaction;

import org.codeus.leaf_transaction.dao.ParticipantDao;
import org.codeus.leaf_transaction.dao.SimpleParticipantDao;
import org.codeus.leaf_transaction.datasource.SingleConnectionDataSource;
import org.codeus.leaf_transaction.exception.BeanNotFoundException;

/**
 * A Leaf representation of the Spring's IoC container (ApplicationContext) that instantiate and customizes Beans
 * Provides a limited set of methods.
 */
public class ApplicationContext {

  private final SingleConnectionDataSource dataSource;
  private final ParticipantDao participantDao;

  public ApplicationContext(SingleConnectionDataSource dataSource) {
    this(dataSource, new SimpleParticipantDao(dataSource));
  }

  public ApplicationContext(SingleConnectionDataSource dataSource, ParticipantDao participantDao) {
    this.dataSource = dataSource;
    this.participantDao = participantDao;
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
    if (NonTransactionalService.class.isAssignableFrom(beanClass)) {
      return (T) new NonTransactionalSampleService(participantDao);
    } else if (ParticipantService.class.isAssignableFrom(beanClass)) {
      return (T) new ParticipantStorageService(participantDao, dataSource);
    } else {
      throw new BeanNotFoundException(beanClass);
    }
  }
}
