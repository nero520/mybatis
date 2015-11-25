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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

/*
 * SqlSessionFactoryBuilder ： SqlSessionFactory的构造器，用于创建SqlSessionFactory，采用了Builder设计模式一
 * Builder模式应用1： SqlSessionFactory的创建
 * 由于构造时参数不定，可以为其创建一个构造器Builder，将SqlSessionFactory的构建过程和表示分开：
 * MyBatis将SqlSessionFactoryBuilder和SqlSessionFactory相互独立。
 * 建造者模式：是将一个复杂的对象的构建与它的表示分离，使得同样的构建过程可以创建不同的表示。
	建造者模式通常包括下面几个角色：	
	1. builder：给出一个抽象接口，以规范产品对象的各个组成成分的建造。这个接口规定要实现复杂对象的哪些部分的创建，并不涉及具体的对象部件的创建。	
	2. ConcreteBuilder：实现Builder接口，针对不同的商业逻辑，具体化复杂对象的各部分的创建。 在建造过程完成后，提供产品的实例。	
	3. Director：调用具体建造者来创建复杂对象的各个部分，在指导者中不涉及具体产品的信息，只负责保证对象各部分完整创建或按某种顺序创建。	
	4. Product：要创建的复杂对象。
	使用建造者模式的好处：
    1.使用建造者模式可以使客户端不必知道产品内部组成的细节。
    2.具体的建造者类之间是相互独立的，对系统的扩展非常有利。
    3.由于具体的建造者是独立的，因此可以对建造过程逐步细化，而不对其他的模块产生任何影响。
         使用建造模式的场合：
    1.创建一些复杂的对象时，这些对象的内部组成构件间的建造顺序是稳定的，但是对象的内部组成构件面临着复杂的变化。
    2.要创建的复杂对象的算法，独立于该对象的组成部分，也独立于组成部分的装配方法时。
   mybatis 初始化要经过简单的以下几步
   1.  调用 SqlSessionFactoryBuilder 对象的 build(inputStream) 方法；
   2.  SqlSessionFactoryBuilder 会根据输入流 inputStream 等信息创建 XMLConfigBuilder 对象 ;
   3.  SqlSessionFactoryBuilder 调用 XMLConfigBuilder 对象的 parse() 方法；
   4.  XMLConfigBuilder 对象返回 Configuration 对象；
   5.  SqlSessionFactoryBuilder 根据 Configuration 对象创建一个 DefaultSessionFactory 对象；
   6.  SqlSessionFactoryBuilder 返回 DefaultSessionFactory 对象给 Client ，供 Client 使用。
 * @author Clinton Begin
 */
public class SqlSessionFactoryBuilder {

  public SqlSessionFactory build(Reader reader) {
    return build(reader, null, null);
  }

  public SqlSessionFactory build(Reader reader, String environment) {
    return build(reader, environment, null);
  }

  public SqlSessionFactory build(Reader reader, Properties properties) {
    return build(reader, null, properties);
  }

  public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
    try {
      //2. 创建XMLConfigBuilder对象用来解析XML配置文件，生成Configuration对象
      XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);
      //3. 将XML配置文件内的信息解析成Java对象Configuration对象，根据Configuration对象创建出SqlSessionFactory对象  
      return build(parser.parse());
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        reader.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }

  public SqlSessionFactory build(InputStream inputStream) {
    return build(inputStream, null, null);
  }

  public SqlSessionFactory build(InputStream inputStream, String environment) {
    return build(inputStream, environment, null);
  }

  public SqlSessionFactory build(InputStream inputStream, Properties properties) {
    return build(inputStream, null, properties);
  }

  public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
    try {
     //1. XMLConfigBuilder会将XML配置文件的信息转换为Document对象，
     //	 而XML配置定义文件DTD转换成XMLMapperEntityResolver对象，
      //然后将二者封装到XpathParser对象中，XpathParser的作用是提供根据Xpath表达式获取基本的DOM节点Node信息的操作
      XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
      return build(parser.parse());
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        inputStream.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }
  
  /**
   * MyBatis内部通过Configuration对象来创建SqlSessionFactory,
   * 用户也可以自己通过API构造好Configuration对象，调用此方法创建SqlSessionFactory  
   * @param config
   * @return
   */
  public SqlSessionFactory build(Configuration config) {
    return new DefaultSqlSessionFactory(config);
  }

}
