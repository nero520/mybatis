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
package org.apache.ibatis.cache;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * SPI for cache providers.
 * 
 * One instance of cache will be created for each namespace.
 * 
 * The cache implementation must have a constructor that receives the cache id as an String parameter.
 * 
 * MyBatis will pass the namespace as id to the constructor.
 * 
 * <pre>
 * public MyCache(final String id) {
 *  if (id == null) {
 *    throw new IllegalArgumentException("Cache instances require an ID");
 *  }
 *  this.id = id;
 *  initialize();
 * }
 * </pre>
 * 
 * 采用装饰模式，一个个包装起来，形成一个链，个人认为是责任链模型，典型的就是SynchronizedCache->LoggingCache->SerializedCache->LruCache->PerpetualCache，通过链起来达到功能增加 
 * SynchronizedCache 同步缓存，防止多线程问题。核心: 加读写锁，
 * ReadWriteLock.readLock().lock()/unlock() 
 * ReadWriteLock.writeLock().lock()/unlock()
 * LoggingCache 日志缓存，添加功能：取缓存时打印命中率
 * SerializedCache 序列化缓存 用途是先将对象序列化成2进制，再缓存
 * LruCache 最近最少使用缓存，核心就是覆盖 LinkedHashMap.removeEldestEntry方法,返回true或false告诉 LinkedHashMap要不要删除此最老键值。LinkedHashMap内部其实就是每次访问或者插入一个元素都会把元素放到链表末尾，这样不经常访问的键值肯定就在链表开头啦。
 * PerpetualCache 永久缓存，一旦存入就一直保持，内部就是一个HashMap,所有方法基本就是直接调用HashMap的方法
 * FifoCache 先进先出缓存，内部就是一个链表，将链表开头元素（最老）移除
 * ScheduledCache 定时调度缓存， 目的是每一小时清空一下缓存
 * SoftCache 软引用缓存，核心是SoftReference
 * WeakCache 弱引用缓存，核心是WeakReference
 * TransactionalCache 事务缓存，一次性存入多个缓存，移除多个缓存
 *
 * @author Clinton Begin
 */

public interface Cache {

  /**
   * @return The identifier of this cache
   */
  String getId();

  /**
   * @param key Can be any object but usually it is a {@link CacheKey}
   * @param value The result of a select.
   */
  void putObject(Object key, Object value);

  /**
   * @param key The key
   * @return The object stored in the cache.
   */
  Object getObject(Object key);

  /**
   * As of 3.3.0 this method is only called during a rollback 
   * for any previous value that was missing in the cache.
   * This lets any blocking cache to release the lock that 
   * may have previously put on the key.
   * A blocking cache puts a lock when a value is null 
   * and releases it when the value is back again.
   * This way other threads will wait for the value to be 
   * available instead of hitting the database.
   *
   * 
   * @param key The key
   * @return Not used
   */
  Object removeObject(Object key);

  /**
   * Clears this cache instance
   */  
  void clear();

  /**
   * Optional. This method is not called by the core.
   * 
   * @return The number of elements stored in the cache (not its capacity).
   */
  int getSize();
  
  /** 
   * Optional. As of 3.2.6 this method is no longer called by the core.
   *  
   * Any locking needed by the cache must be provided internally by the cache provider.
   * 
   * @return A ReadWriteLock 
   */
  ReadWriteLock getReadWriteLock();

}