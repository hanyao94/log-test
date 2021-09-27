/**
 * 项目名：	test-starter
 * 文件名：	ContentCachingRequestWrapper.java
 * 模块说明：
 * 修改历史：
 * 2021/9/27 - seven - 创建。
 */
package com.seven.test.infrastructure.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author seven
 */
@Slf4j
public class ContentCachingRequestWrapper extends HttpServletRequestWrapper {

  private final String body;

  public String getBody() {
    return body;
  }

  public ContentCachingRequestWrapper(HttpServletRequest request) {
    super(request);
    this.body = getBodyString(request);
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return super.getReader();
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body == null ? "".getBytes() : body.getBytes(getCharacterEncoding()));
    return new ServletInputStream() {
      @Override
      public boolean isFinished() {
        return false;
      }

      @Override
      public boolean isReady() {
        return false;
      }

      @Override
      public void setReadListener(ReadListener readListener) {

      }

      @Override
      public int read() {
        return byteArrayInputStream.read();
      }
    };
  }

  private String getBodyString(HttpServletRequest request) {
    String body = null;
    try (ServletInputStream inputStream = request.getInputStream()) {
      body = IOUtils.toString(inputStream, getCharacterEncoding());
    } catch (IOException e) {
      log.error("request获取body失败", e);
    }

    return body;
  }
}
