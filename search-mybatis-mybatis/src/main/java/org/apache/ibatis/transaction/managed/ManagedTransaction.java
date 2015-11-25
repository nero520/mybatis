/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.transaction.managed;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;

/**
 * {@link Transaction} that lets the container manage the full lifecycle of the transaction.
 * Delays connection retrieval until getConnection() is called.
 * Ignores all commit or rollback requests.
 * By default, it closes the connection but can be configured not to do it.
 * 使用MANAGED的事务管理机制：这种机制MyBatis自身不会去实现事务管理，
 * 而是让程序的容器如（JBOSS，Weblogic）来实现对事务的管理,
 * ManagedTransaction让容器来管理事务Transaction的整个生命周期，意思就是说，
 * 使用ManagedTransaction的commit和rollback功能不会对事务有任何的影响，
 * 它什么都不会做，它将事务管理的权利移交给了容器来实现。
 * @see ManagedTransactionFactory
 */
/**
 * @author Clinton Begin
 */
public class ManagedTransaction implements Transaction {

  private static final Log log = LogFactory.getLog(ManagedTransaction.class);

  private DataSource dataSource;
  private TransactionIsolationLevel level;
  private Connection connection;
  private boolean closeConnection;

  public ManagedTransaction(Connection connection, boolean closeConnection) {
    this.connection = connection;
    this.closeConnection = closeConnection;
  }

  public ManagedTransaction(DataSource ds, TransactionIsolationLevel level, boolean closeConnection) {
    this.dataSource = ds;
    this.level = level;
    this.closeConnection = closeConnection;
  }

  @Override
  public Connection getConnection() throws SQLException {
    if (this.connection == null) {
      openConnection();
    }
    return this.connection;
  }
  /**
   * 注意：如果我们使用MyBatis构建本地程序，即不是WEB程序，
   * 若将type设置成"MANAGED"，那么，我们执行的任何update操作，
   * 即使我们最后执行了commit操作，数据也不会保留，不会对数据库造成任何影响。
   * 因为我们将MyBatis配置成了“MANAGED”，即MyBatis自己不管理事务，
   * 而我们又是运行的本地程序，没有事务管理功能，所以对数据库的update操作都是无效的。
   * 由容器自己的来管理
   */
  @Override
  public void commit() throws SQLException {
    // Does nothing
  }

  /**
   * 由容器自身的来管理
   */
  @Override
  public void rollback() throws SQLException {
    // Does nothing
  }

  @Override
  public void close() throws SQLException {
    if (this.closeConnection && this.connection != null) {
      if (log.isDebugEnabled()) {
        log.debug("Closing JDBC Connection [" + this.connection + "]");
      }
      this.connection.close();
    }
  }

  protected void openConnection() throws SQLException {
    if (log.isDebugEnabled()) {
      log.debug("Opening JDBC Connection");
    }
    this.connection = this.dataSource.getConnection();
    if (this.level != null) {
      this.connection.setTransactionIsolation(this.level.getLevel());
    }
  }

}
