package org.codeus.leaf_transaction;

import org.codeus.leaf_transaction.annotations.Service;
import org.codeus.leaf_transaction.dao.ParticipantDao;
import org.codeus.leaf_transaction.datasource.SingleConnectionDataSource;
import org.codeus.leaf_transaction.model.Participant;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Simple service that performs transactional communication with DB via DAO.
 */
@Service
public class ParticipantStorageService implements ParticipantService {

  private final ParticipantDao participantDao;
  private final SingleConnectionDataSource dataSource;

  public ParticipantStorageService(ParticipantDao participantDao, SingleConnectionDataSource dataSource) {
    this.participantDao = participantDao;
    this.dataSource = dataSource;
  }

  @Override
  public void processParticipant(Participant participant) {
    Connection connection = dataSource.getConnection();
    System.out.println("Transaction started");
    try {
      connection.setAutoCommit(false);
      System.out.println("Transaction started");

      System.out.println("Executing Participant processing...");
      participantDao.save(participant);

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
    System.out.printf("Retrieving Participant data by id=%s...%n", id);
    return participantDao.getById(id);
  }
}
