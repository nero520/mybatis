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
package org.apache.ibatis.datasource.pooled;

import java.util.ArrayList;
import java.util.List;

/**
 * PooledDataSource将java.sql.Connection对象包裹成PooledConnection对象放到了PoolState类型的容器中维护。
 *  MyBatis将连接池中的PooledConnection分为两种状态： 空闲状态（idle）和活动状态(active)，
 *  这两种状态的PooledConnection对象分别被存储到PoolState容器内的idleConnections和activeConnections两个List集合中：
 * @author Clinton Begin
 */
public class PoolState {

  protected PooledDataSource dataSource;

  /**
   * 空闲(idle)状态PooledConnection对象被放置到此集合中，表示当前闲置的没有被使用的PooledConnection集合，
   * 调用PooledDataSource的getConnection()方法时，会优先从此集合中取PooledConnection对象。
   * 当用完一个java.sql.Connection对象时，MyBatis会将其包裹成PooledConnection对象放到此集合中。
   */
  protected final List<PooledConnection> idleConnections = new ArrayList<PooledConnection>();
  /**
   * 活动(active)状态的PooledConnection对象被放置到名为activeConnections的ArrayList中，表示当前正在被使用的PooledConnection集合，
   * 调用PooledDataSource的getConnection()方法时，会优先从idleConnections集合中取PooledConnection对象,
   * 如果没有，则看此集合是否已满，如果未满，PooledDataSource会创建出一个PooledConnection，添加到此集合中，并返回。
   */
  protected final List<PooledConnection> activeConnections = new ArrayList<PooledConnection>();
  protected long requestCount = 0;
  protected long accumulatedRequestTime = 0;
  protected long accumulatedCheckoutTime = 0;
  protected long claimedOverdueConnectionCount = 0;
  protected long accumulatedCheckoutTimeOfOverdueConnections = 0;
  protected long accumulatedWaitTime = 0;
  protected long hadToWaitCount = 0;
  protected long badConnectionCount = 0;

  public PoolState(PooledDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public synchronized long getRequestCount() {
    return requestCount;
  }

  public synchronized long getAverageRequestTime() {
    return requestCount == 0 ? 0 : accumulatedRequestTime / requestCount;
  }

  public synchronized long getAverageWaitTime() {
    return hadToWaitCount == 0 ? 0 : accumulatedWaitTime / hadToWaitCount;

  }

  public synchronized long getHadToWaitCount() {
    return hadToWaitCount;
  }

  public synchronized long getBadConnectionCount() {
    return badConnectionCount;
  }

  public synchronized long getClaimedOverdueConnectionCount() {
    return claimedOverdueConnectionCount;
  }

  public synchronized long getAverageOverdueCheckoutTime() {
    return claimedOverdueConnectionCount == 0 ? 0 : accumulatedCheckoutTimeOfOverdueConnections / claimedOverdueConnectionCount;
  }

  public synchronized long getAverageCheckoutTime() {
    return requestCount == 0 ? 0 : accumulatedCheckoutTime / requestCount;
  }


  /**
   * 最大空闲连接数
   * @return
   */
  public synchronized int getIdleConnectionCount() {
    return idleConnections.size();
  }

  /**
   * 最大活动连接数
   * @return
   */
  public synchronized int getActiveConnectionCount() {
    return activeConnections.size();
  }

  @Override
  public synchronized String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("\n===CONFINGURATION==============================================");
    builder.append("\n jdbcDriver                     ").append(dataSource.getDriver());
    builder.append("\n jdbcUrl                        ").append(dataSource.getUrl());
    builder.append("\n jdbcUsername                   ").append(dataSource.getUsername());
    builder.append("\n jdbcPassword                   ").append((dataSource.getPassword() == null ? "NULL" : "************"));
    builder.append("\n poolMaxActiveConnections       ").append(dataSource.poolMaximumActiveConnections);
    builder.append("\n poolMaxIdleConnections         ").append(dataSource.poolMaximumIdleConnections);
    builder.append("\n poolMaxCheckoutTime            ").append(dataSource.poolMaximumCheckoutTime);
    builder.append("\n poolTimeToWait                 ").append(dataSource.poolTimeToWait);
    builder.append("\n poolPingEnabled                ").append(dataSource.poolPingEnabled);
    builder.append("\n poolPingQuery                  ").append(dataSource.poolPingQuery);
    builder.append("\n poolPingConnectionsNotUsedFor  ").append(dataSource.poolPingConnectionsNotUsedFor);
    builder.append("\n ---STATUS-----------------------------------------------------");
    builder.append("\n activeConnections              ").append(getActiveConnectionCount());
    builder.append("\n idleConnections                ").append(getIdleConnectionCount());
    builder.append("\n requestCount                   ").append(getRequestCount());
    builder.append("\n averageRequestTime             ").append(getAverageRequestTime());
    builder.append("\n averageCheckoutTime            ").append(getAverageCheckoutTime());
    builder.append("\n claimedOverdue                 ").append(getClaimedOverdueConnectionCount());
    builder.append("\n averageOverdueCheckoutTime     ").append(getAverageOverdueCheckoutTime());
    builder.append("\n hadToWait                      ").append(getHadToWaitCount());
    builder.append("\n averageWaitTime                ").append(getAverageWaitTime());
    builder.append("\n badConnectionCount             ").append(getBadConnectionCount());
    builder.append("\n===============================================================");
    return builder.toString();
  }

}
