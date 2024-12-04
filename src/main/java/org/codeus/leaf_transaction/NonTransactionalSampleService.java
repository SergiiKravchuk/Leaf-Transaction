package org.codeus.leaf_transaction;

import org.codeus.leaf_transaction.annotations.Service;
import org.codeus.leaf_transaction.dao.ParticipantDao;
import org.codeus.leaf_transaction.model.Participant;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Simple service that performs NON-transactional communication with DB via DAO.
 */
@Service
public class NonTransactionalSampleService implements NonTransactionalService {

  private final ParticipantDao participantDao;

  public NonTransactionalSampleService(ParticipantDao participantDao) {
    this.participantDao = participantDao;
  }

  @Override
  public Map<String, Double> getParticipantStats() {
    System.out.printf("%s performing stats collection%n", this.getClass().getName());
    return participantDao.getAllParticipants().stream()
      .collect(Collectors.groupingBy(Participant::company, Collectors.averagingInt(Participant::age)));
  }
}
