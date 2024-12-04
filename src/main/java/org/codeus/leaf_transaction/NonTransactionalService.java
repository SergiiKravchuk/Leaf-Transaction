package org.codeus.leaf_transaction;

import java.util.Map;

public interface NonTransactionalService {

  Map<String, Double> getParticipantStats();
}
