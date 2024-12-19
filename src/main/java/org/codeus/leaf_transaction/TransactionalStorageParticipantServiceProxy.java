package org.codeus.leaf_transaction;

import org.codeus.leaf_transaction.annotations.Transactional;
import org.codeus.leaf_transaction.datasource.SingleConnectionDataSource;
import org.codeus.leaf_transaction.model.Participant;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Simple Proxy that adds JDBC transaction control logic for methods annotated with {@link Transactional} annotation.
 */
public class TransactionalStorageParticipantServiceProxy implements ParticipantService {

  private final ParticipantStorageService targetService;

  private final SingleConnectionDataSource singleConnectionDataSource;

  public TransactionalStorageParticipantServiceProxy(ParticipantStorageService targetService, SingleConnectionDataSource singleConnectionDataSource) {
    this.targetService = targetService;
    this.singleConnectionDataSource = singleConnectionDataSource;
  }

  @Override
  public void processParticipant(Participant participant) {

    Connection connection = singleConnectionDataSource.getConnection();
    System.out.println("Transaction started");
    try {
      connection.setAutoCommit(false);
      System.out.println("Transaction started");
      targetService.processParticipant(participant);
      connection.commit();
      System.out.println("Transaction committed");
    } catch (Exception exception) {
      try {
        connection.rollback();
        System.out.println("Transaction rolled back");
        throw new RuntimeException("Exception during participant processing", exception);
      } catch (SQLException innerEx) {
        throw new RuntimeException("Failed to rollback logic failed", innerEx);
      }
    }
  }

  @Override
  public Participant getParticipantById(String id) {
    return targetService.getParticipantById(id);
  }
}
