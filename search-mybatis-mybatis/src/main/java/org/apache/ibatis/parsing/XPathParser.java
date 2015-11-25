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
package org.apache.ibatis.parsing;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.ibatis.builder.BuilderException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XpathParser的作用是提供根据Xpath表达式获取基本的DOM节点Node信息的操作
 * @author Clinton Begin
 */
public class XPathParser {

  private Document document;
  private boolean validation;
  private EntityResolver entityResolver;
  private Properties variables;
  private XPath xpath;

  public XPathParser(String xml) {
    commonConstructor(false, null, null);
    this.document = createDocument(new InputSource(new StringReader(xml)));
  }

  public XPathParser(Reader reader) {
    commonConstructor(false, null, null);
    this.document = createDocument(new InputSource(reader));
  }

  public XPathParser(InputStream inputStream) {
    commonConstructor(false, null, null);
    this.document = createDocument(new InputSource(inputStream));
  }

  public XPathParser(Document document) {
    commonConstructor(false, null, null);
    this.document = document;
  }

  public XPathParser(String xml, boolean validation) {
    commonConstructor(validation, null, null);
    this.document = createDocument(new InputSource(new StringReader(xml)));
  }

  public XPathParser(Reader reader, boolean validation) {
    commonConstructor(validation, null, null);
    this.document = createDocument(new InputSource(reader));
  }

  public XPathParser(InputStream inputStream, boolean validation) {
    commonConstructor(validation, null, null);
    this.document = createDocument(new InputSource(inputStream));
  }

  public XPathParser(Document document, boolean validation) {
    commonConstructor(validation, null, null);
    this.document = document;
  }

  public XPathParser(String xml, boolean validation, Properties variables) {
    commonConstructor(validation, variables, null);
    this.document = createDocument(new InputSource(new StringReader(xml)));
  }

  public XPathParser(Reader reader, boolean validation, Properties variables) {
    commonConstructor(validation, variables, null);
    this.document = createDocument(new InputSource(reader));
  }

  public XPathParser(InputStream inputStream, boolean validation, Properties variables) {
    commonConstructor(validation, variables, null);
    this.document = createDocument(new InputSource(inputStream));
  }

  public XPathParser(Document document, boolean validation, Properties variables) {
    commonConstructor(validation, variables, null);
    this.document = document;
  }

  public XPathParser(String xml, boolean validation, Properties variables, EntityResolver entityResolver) {
    commonConstructor(validation, variables, entityResolver);
    this.document = createDocument(new InputSource(new StringReader(xml)));
  }

  public XPathParser(Reader reader, boolean validation, Properties variables, EntityResolver entityResolver) {
    commonConstructor(validation, variables, entityResolver);
    this.document = createDocument(new InputSource(reader));
  }

  public XPathParser(InputStream inputStream, boolean validation, Properties variables, EntityResolver entityResolver) {
	commonConstructor(validation, variables, entityResolver);
    this.document = createDocument(new InputSource(inputStream));
  }

  public XPathParser(Document document, boolean validation, Properties variables, EntityResolver entityResolver) {
    commonConstructor(validation, variables, entityResolver);
    this.document = document;
  }

  public void setVariables(Properties variables) {
    this.variables = variables;
  }

  /**
   *evalXXX函数有两种多态形式： 一种是只有一个expression参数；
   * @param expression
   * @return
   */
  public String evalString(String expression) {
	  //设置类中的document属性作为root，
    return evalString(document, expression);
  }
  /**
   * evalXXX函数有两种多态形式：除了expression参数外还包含一个root参数。
   * 像我们经常见到的那样，带一个参数的evalXXX函数会在设置一个默认值后调用带有两个参数的函数，
   * @param root
   * @param expression
   * @return
   */
  public String evalString(Object root, String expression) {
    String result = (String) evaluate(expression, root, XPathConstants.STRING);
    result = PropertyParser.parse(result, variables);
    return result;
  }

  public Boolean evalBoolean(String expression) {
    return evalBoolean(document, expression);
  }

  public Boolean evalBoolean(Object root, String expression) {
    return (Boolean) evaluate(expression, root, XPathConstants.BOOLEAN);
  }

  public Short evalShort(String expression) {
    return evalShort(document, expression);
  }

  public Short evalShort(Object root, String expression) {
    return Short.valueOf(evalString(root, expression));
  }

  public Integer evalInteger(String expression) {
    return evalInteger(document, expression);
  }

  public Integer evalInteger(Object root, String expression) {
    return Integer.valueOf(evalString(root, expression));
  }

  public Long evalLong(String expression) {
    return evalLong(document, expression);
  }

  public Long evalLong(Object root, String expression) {
    return Long.valueOf(evalString(root, expression));
  }

  public Float evalFloat(String expression) {
    return evalFloat(document, expression);
  }

  public Float evalFloat(Object root, String expression) {
    return Float.valueOf(evalString(root, expression));
  }

  public Double evalDouble(String expression) {
    return evalDouble(document, expression);
  }

  public Double evalDouble(Object root, String expression) {
    return (Double) evaluate(expression, root, XPathConstants.NUMBER);
  }

  public List<XNode> evalNodes(String expression) {
    return evalNodes(document, expression);
  }

  public List<XNode> evalNodes(Object root, String expression) {
    List<XNode> xnodes = new ArrayList<XNode>();
    NodeList nodes = (NodeList) evaluate(expression, root, XPathConstants.NODESET);
    for (int i = 0; i < nodes.getLength(); i++) {
      xnodes.add(new XNode(this, nodes.item(i), variables));
    }
    return xnodes;
  }

  public XNode evalNode(String expression) {
    return evalNode(document, expression);
  }

  public XNode evalNode(Object root, String expression) {
    Node node = (Node) evaluate(expression, root, XPathConstants.NODE);
    if (node == null) {
      return null;
    }
    return new XNode(this, node, variables);
  }

  private Object evaluate(String expression, Object root, QName returnType) {
    try {
     //调用xpath类进行相应的解析。
     //注意returnType参数，虽然evaluate返回的数据类型是Object的，
     //但是如果指定了错误的returnType，那么在进行类型转换时将会报类型转换异常
      return xpath.evaluate(expression, root, returnType);
    } catch (Exception e) {
      throw new BuilderException("Error evaluating XPath.  Cause: " + e, e);
    }
  }

  private Document createDocument(InputSource inputSource) {
    // important: this must only be called AFTER common constructor
    // 那为什么必须在调用commonConstructor函数后才能调用这个函数呢？因为这个函数里面用到了两个属性：validation和entityResolver
	// 如果在这两个属性没有设置前就调用这个函数，就可能会导致这个类内部属性冲突
    try {
    	//创建document时用到了两个类：DocumentBuilderFactory和DocumentBuilder。
    	//DocumentBuilderFactory.newInstance()创建DocumentBuilderFactory实现类的对象，它会通过一下方式来查找实现类：
    	//1.在系统环境变量中(System.getProperties())中查找 key=javax.xml.parsers.DocumentBuilderFactory
    	//2.如果1没有找到，则找java.home/lib/jaxp.properties 文件，如果文件存在，在文件中查找key=javax.xml.parsers.DocumentBuilderFactory
    	//3.如果2没有找到,则在classpath中的所有的jar包中查找META-INF/services /javax.xml.parsers.DocumentBuilderFactory 文件
    	//    全都没找到，则返回null
    	//如果上面都没有找到，那么就使用JDK自带的实现类：
    	//com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl
    	//在创建DocumentBuilder实例的时候，是根据DocumentBuilderFactoryImpl的不同有不同的实现。
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(validation);

      factory.setNamespaceAware(false);
      factory.setIgnoringComments(true);
      factory.setIgnoringElementContentWhitespace(false);
      factory.setCoalescing(false);
      factory.setExpandEntityReferences(true);
      //XML配置定义文件DTD转换成XMLMapperEntityResolver对象，然后将二者封装到XpathParser对象中，
  	  //XpathParser的作用是提供根据Xpath表达式获取基本的DOM节点Node信息的操作。
      DocumentBuilder builder = factory.newDocumentBuilder();
      //为了在网络不可用的情况下，正常解析XML文件，我们可以在使用builder之前，设置EntityResolver
      builder.setEntityResolver(entityResolver);
      builder.setErrorHandler(new ErrorHandler() {
        @Override
        public void error(SAXParseException exception) throws SAXException {
          throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
          throw exception;
        }

        @Override
        public void warning(SAXParseException exception) throws SAXException {
        }
      });
      return builder.parse(inputSource);
    } catch (Exception e) {
      throw new BuilderException("Error creating document instance.  Cause: " + e, e);
    }
  }

  private void commonConstructor(boolean validation, Properties variables, EntityResolver entityResolver) {
	 //初始化这个类的基本属性
    this.validation = validation;
    this.entityResolver = entityResolver;
    this.variables = variables;
    //利用XPathFactory创建一个新的xpath对象
    XPathFactory factory = XPathFactory.newInstance();
    this.xpath = factory.newXPath();
  }

}
