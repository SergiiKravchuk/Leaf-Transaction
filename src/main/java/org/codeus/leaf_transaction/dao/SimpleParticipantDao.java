package org.codeus.leaf_transaction.dao;

import org.codeus.leaf_transaction.datasource.SingleConnectionDataSource;
import org.codeus.leaf_transaction.model.Participant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Simple DAO that handles DB queries for {@link Participant} entity.
 */
public class SimpleParticipantDao implements ParticipantDao {

  private final SingleConnectionDataSource dataSource;

  public SimpleParticipantDao(SingleConnectionDataSource dataSource) {
    this.dataSource = dataSource;
  }

    public void save(Participant participant) {
    try {
      Connection connection = dataSource.getConnection();
      PreparedStatement statement = connection.prepareStatement("insert into participants(name, city, company, position, age) values (?, ?, ?, ?, ?, ?)");
      statement.setString(1, participant.name());
      statement.setString(2, participant.city());
      statement.setString(3, participant.company());
      statement.setString(4, participant.position());
      statement.setInt(5, participant.age());

      statement.executeUpdate();
      simulateDbConstraint(participant);

    } catch (SQLException | IllegalArgumentException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Participant getById(String id) {
    try {
      Connection connection = dataSource.getConnection();
      PreparedStatement statement = connection.prepareStatement("select * from participants p where p.id=?");
      statement.setString(1, id);

      ResultSet rs = statement.executeQuery();
      return mapParticipant(rs);

    } catch (SQLException | IllegalArgumentException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<Participant> getAllParticipants() {
    try {
      Connection connection = dataSource.getConnection();
      PreparedStatement statement = connection.prepareStatement("select p.company, p.age from participants p");

      ResultSet rs = statement.executeQuery();
      return mapParticipants(rs);

    } catch (SQLException | IllegalArgumentException e) {
      throw new RuntimeException(e);
    }
  }

  //public for test purposes
  public List<Participant> mapParticipants(ResultSet rs) throws SQLException {
    List<Participant> participants = new ArrayList<>();
    while(rs.next()) participants.add(mapParticipant(rs));
    return participants;
  }

  //public for test purposes
  public Participant mapParticipant(ResultSet rs) throws SQLException {
    return new Participant(
      rs.getString("name"),
      rs.getString("city"),
      rs.getString("company"),
      rs.getString("position"),
      rs.getInt("age"));
  }

  private void simulateDbConstraint(Participant participant) throws SQLException {
    Objects.requireNonNull(participant, "User data should not be `null`");
    Objects.requireNonNull(participant.name(), "User name should not be `null`");
    if (participant.name().isEmpty()) throw new SQLException("User name should not be empty");
    if (participant.age() < 18) throw new SQLException("User age should lower than 18");
  }
}
