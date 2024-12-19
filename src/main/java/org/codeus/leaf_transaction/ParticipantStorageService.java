package org.codeus.leaf_transaction;

import org.codeus.leaf_transaction.annotations.Transactional;
import org.codeus.leaf_transaction.annotations.Service;
import org.codeus.leaf_transaction.dao.ParticipantDao;
import org.codeus.leaf_transaction.model.Participant;

/**
 * Simple service that performs transactional communication with DB via DAO.
 */
@Service
public class ParticipantStorageService implements ParticipantService {

  private final ParticipantDao participantDao;

  public ParticipantStorageService(ParticipantDao participantDao) {
    this.participantDao = participantDao;
  }

  @Transactional
  @Override
  public void processParticipant(Participant participant) {
    System.out.println("Executing Participant processing...");
    participantDao.save(participant);
  }

  @Override
  public Participant getParticipantById(String id) {
    System.out.printf("Retrieving Participant data by id=%s...%n", id);
    return participantDao.getById(id);
  }
}
