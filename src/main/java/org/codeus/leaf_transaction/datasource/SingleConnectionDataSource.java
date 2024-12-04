package org.codeus.leaf_transaction.datasource;

import java.sql.Connection;

/**
 * Simple DataSource that holds a single DB connection.
 */
public class SingleConnectionDataSource {

  private final Connection connection;

  public SingleConnectionDataSource(Connection connection) {
    this.connection = connection;
  }

  public Connection getConnection() {
    return connection;
  }
}
