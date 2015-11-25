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
package org.apache.ibatis.session;

/**
 * 指定MyBatis 是否并且如何来自动映射数据表字段与对象的属性。PARTIAL将只自动映射简单的，没有嵌套的结果。FULL 将自动映射所有复杂的结果。
 * Specifies if and how MyBatis should automatically map columns to fields/properties.
 * 
 * @author Eduardo Macarron
 */
public enum AutoMappingBehavior {

  /**
   * Disables auto-mapping.
   */
  NONE,

  /**
   * Will only auto-map results with no nested result mappings defined inside.
   */
  PARTIAL,

  /**
   * Will auto-map result mappings of any complexity (containing nested or otherwise).
   */
  FULL
}
