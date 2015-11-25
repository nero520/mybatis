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

/**
 * @author Clinton Begin
 * 这个类是对常用Token进行parser的类
 */
public class GenericTokenParser {

  private final String openToken;//开始标识
  private final String closeToken;//结束标识
  private final TokenHandler handler;//token处理器

  /**
   * 利用带参数的构造函数初始化各项属性
   * @param openToken 
   * @param closeToken
   * @param handler
   */
  public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
    this.openToken = openToken;
    this.closeToken = closeToken;
    this.handler = handler;
  }

  /**
   * 将openToken和endToken间的字符串取出来用handler处理下，然后再拼接到一块
   * @param text
   * @return
   */
  public String parse(String text) {
    StringBuilder builder = new StringBuilder();
    if (text != null && text.length() > 0) {//如果传入的字符串有值
      //将字符串转换成字符数组
      char[] src = text.toCharArray();
      int offset = 0;
      //判断openToken在text中的位置，注意indexOf函数的返回值-1表示不存在，0表示在在开头的位置
      int start = text.indexOf(openToken, offset);
      while (start > -1) {
        if (start > 0 && src[start - 1] == '\\') { //如果text中在openToken前存在转义符就将转义符去掉。如果openToken前存在转义符，start的值必然大于0，最小也为1         
          //因为此时openToken是不需要进行处理的，所以也不需要处理endToken。接着查找下一个openToken
          // the variable is escaped. remove the backslash.
          builder.append(src, offset, start - offset - 1).append(openToken);
          offset = start + openToken.length();//重设offset
        } else {//如果不存在openToken，则直接将offset位置后的字符添加到builder中
          int end = text.indexOf(closeToken, start);
          if (end == -1) {
            builder.append(src, offset, src.length - offset);
            offset = src.length;//重设offset
          } else {
            builder.append(src, offset, start - offset);//添加openToken前offset后位置的字符到bulider中
            offset = start + openToken.length();//重设offset
            String content = new String(src, offset, end - offset);//获取openToken和endToken位置间的字符串
            builder.append(handler.handleToken(content));//调用handler进行处理
            offset = end + closeToken.length();//重设offset
          }
        }
        start = text.indexOf(openToken, offset);//开始下一个循环
      }
      if (offset < src.length) {//只有当text中不存在openToken且text.length大于0时才会执行下面的语句
        builder.append(src, offset, src.length - offset);
      }
    }
    return builder.toString();
  }

}
