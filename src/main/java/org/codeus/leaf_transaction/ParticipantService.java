package org.codeus.leaf_transaction;

import org.codeus.leaf_transaction.model.Participant;

public interface ParticipantService {

  void processParticipant(Participant participant);
  Participant getParticipantById(String id);
}
