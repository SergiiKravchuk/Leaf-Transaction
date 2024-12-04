package org.codeus.leaf_transaction;

import lombok.SneakyThrows;
import org.assertj.core.api.Assert;
import org.codeus.leaf_transaction.dao.SimpleParticipantDao;
import org.codeus.leaf_transaction.datasource.SingleConnectionDataSource;
import org.codeus.leaf_transaction.exception.BeanNotFoundException;
import org.codeus.leaf_transaction.model.Participant;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationContextTest {

  final Participant validParticipant1 = new Participant("John Doe",
    "New York",
    "Forty-two",
    "Distinguished Engineer",
    42);

  final Participant validParticipant2 = new Participant("Boo Cho",
    "Chicago",
    "Forty-two",
    "Senior Engineer",
    33);

  final Participant invalidParticipant = new Participant("Johnny Boy",
    "North Carolina",
    "Forty-two",
    "Trainee",
    16);

  @SneakyThrows
  @BeforeEach
  public void mockDataSource() {
    mockConnection = mock(Connection.class);
    mockStatement = mock(PreparedStatement.class);
    SingleConnectionDataSource mockDataSource = mock(SingleConnectionDataSource.class);

    when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
    int updatedTableRowCount = 1;
    when(mockStatement.executeUpdate()).thenReturn(updatedTableRowCount);

    when(mockDataSource.getConnection()).thenReturn(mockConnection);

    SimpleParticipantDao spyDao = spy(new SimpleParticipantDao(mockDataSource));
    doReturn(List.of(validParticipant1, validParticipant2)).when(spyDao).mapParticipants(any());//using `any()` to mock `null` input as well
    doReturn(validParticipant1).when(spyDao).mapParticipant(any());//using any() to mock `null` input as well

    applicationContext = new ApplicationContext(mockDataSource, spyDao);
  }

  private PreparedStatement mockStatement;
  private Connection mockConnection;
  private ApplicationContext applicationContext;

  @Test
  @Order(1)
  void testTransactionCommit() throws Exception {
    ParticipantService bean = applicationContext.getBean(ParticipantService.class);

    bean.processParticipant(validParticipant1);

    verify(mockConnection).prepareStatement(anyString());
    verify(mockConnection).commit();
    verify(mockStatement).executeUpdate();
  }

  @Test
  @Order(2)
  void testTransactionRollbackOnException() throws Exception {
    ParticipantService bean = applicationContext.getBean(ParticipantService.class);

    assertThrows(RuntimeException.class, () -> bean.processParticipant(invalidParticipant));

    verify(mockConnection).rollback();
  }

  @Test
  @Order(6)
  void testTransactionalProxyIsNotAppliedForNonMarkedMethods() throws Exception {
    ParticipantService bean = applicationContext.getBean(ParticipantService.class);

    bean.getParticipantById("uuid-42");

    verify(mockConnection).prepareStatement(anyString());
    verifyNoMoreInteractions(mockConnection);
    verify(mockStatement).executeQuery();
  }

  @Test
  @Order(11)
  void testTransactionalProxyIsNotAppliedForNonMarkedServices() throws Exception {
    NonTransactionalService bean = applicationContext.getBean(NonTransactionalService.class);

    bean.getParticipantStats();

    verify(mockConnection).prepareStatement(anyString());
    verifyNoMoreInteractions(mockConnection);
    verify(mockStatement).executeQuery();
  }

  @Test
  @Order(16)
  void testBeanNotFoundExceptionIsThrownForExternal() {
    assertThatExceptionOfType(BeanNotFoundException.class).isThrownBy(() -> applicationContext.getBean(Assert.class));
  }

}
