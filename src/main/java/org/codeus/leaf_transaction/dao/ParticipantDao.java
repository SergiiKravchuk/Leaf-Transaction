package org.codeus.leaf_transaction.dao;

import org.codeus.leaf_transaction.model.Participant;

import java.util.List;

public interface ParticipantDao {

  void save(Participant participant);

  Participant getById(String id);

  List<Participant> getAllParticipants();
}
