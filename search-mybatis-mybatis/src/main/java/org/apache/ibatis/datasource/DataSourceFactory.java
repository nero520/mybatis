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
package org.apache.ibatis.datasource;

import java.util.Properties;
import javax.sql.DataSource;

/**
 * 工厂模式根据抽象程度的不同分为三种：简单工厂模式（也叫静态工厂模式）、抽象工厂模式、本代码所讲述的工厂方法模式
 * MyBatis是通过[工厂模式]来创建数据源DataSource对象的，
 * MyBatis定义了抽象的工厂接口:
 * 在抽象工厂模式中，有一个产品族的概念：
 * 所谓的产品族，是指位于不同产品等级结构中功能相关联的产品组成的家族。
 * 抽象工厂模式所提供的一系列产品就组成一个产品族；
 * 而工厂方法提供的一系列产品称为一个等级结构。
 * org.apache.ibatis.datasource.DataSourceFactory,
 * 通过其getDataSource()方法返回数据源DataSource
 * 工厂方法模式：
       通过工厂方法模式的类图可以看到，工厂方法模式有四个要素：
工厂接口。工厂接口是工厂方法模式的核心，与调用者直接交互用来提供产品。在实际编程中，有时候也会使用一个抽象类来作为与调用者交互的接口，其本质上是一样的。
工厂实现。在编程中，工厂实现决定如何实例化产品，是实现扩展的途径，需要有多少种产品，就需要有多少个具体的工厂实现。
产品接口。产品接口的主要目的是定义产品的规范，所有的产品实现都必须遵循产品接口定义的规范。产品接口是调用者最为关心的，产品接口定义的优劣直接决定了调用者代码的稳定性。同样，产品接口也可以用抽象类来代替，但要注意最好不要违反里氏替换原则。
产品实现。实现产品接口的具体类，决定了产品在客户端中的具体行为。
        前文提到的简单工厂模式跟工厂方法模式极为相似，区别是：简单工厂只有三个要素，他没有工厂接口，并且得到产品的方法一般是静态的。因为没有工厂接口，所以在工厂实现的扩展性方面稍弱，可以算所工厂方法模式的简化版，关于简单工厂模式，在此一笔带过。
      
适用场景：
        不管是简单工厂模式，工厂方法模式还是抽象工厂模式，他们具有类似的特性，所以他们的适用场景也是类似的。
        首先，作为一种创建类模式，在任何需要生成复杂对象的地方，都可以使用工厂方法模式。有一点需要注意的地方就是复杂对象适合使用工厂模式，而简单对象，特别是只需要通过new就可以完成创建的对象，无需使用工厂模式。如果使用工厂模式，就需要引入一个工厂类，会增加系统的复杂度。
       其次，工厂模式是一种典型的解耦模式，迪米特法则在工厂模式中表现的尤为明显。假如调用者自己组装产品需要增加依赖关系时，可以考虑使用工厂模式。将会大大降低对象之间的耦合度。
       再次，由于工厂模式是依靠抽象架构的，它把实例化产品的任务交由实现类完成，扩展性比较好。也就是说，当需要系统有比较好的扩展性时，可以考虑工厂模式，不同的产品用不同的实现工厂来组装。
  -------------------------------------     
 * 抽象工厂模式的优点
        抽象工厂模式除了具有工厂方法模式的优点外，最主要的优点就是可以在类的内部对产品族进行约束。所谓的产品族，一般或多或少的都存在一定的关联，抽象工厂模式就可以在类内部对产品族的关联关系进行定义和描述，而不必专门引入一个新的类来进行管理。
 
抽象工厂模式的缺点
       产品族的扩展将是一件十分费力的事情，假如产品族中需要增加一个新的产品，则几乎所有的工厂类都需要进行修改。所以使用抽象工厂模式时，对产品等级结构的划分是非常重要的。
 
适用场景
       当需要创建的对象是一系列相互关联或相互依赖的产品族时，便可以使用抽象工厂模式。说的更明白一点，就是一个继承体系中，如果存在着多个等级结构（即存在着多个抽象类），并且分属各个等级结构中的实现类之间存在着一定的关联或者约束，就可以使用抽象工厂模式。假如各个等级结构中的实现类之间不存在关联或约束，则使用多个独立的工厂来对产品进行创建，则更合适一点。
 * @author Clinton Begin
 */
public interface DataSourceFactory {

  void setProperties(Properties props);
  //DataSource相当于产品接口；该方法生成数据源相当于生成产品，
  //由于只有一个产品接口，所以只认为是工厂模式而非抽象工程模式，这个是两者本质的区别
  DataSource getDataSource();

}
