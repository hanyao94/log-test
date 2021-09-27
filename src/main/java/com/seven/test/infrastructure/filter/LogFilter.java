/**
 * 项目名：	test-starter
 * 文件名：	LogFilter.java
 * 模块说明：
 * 修改历史：
 * 2021/9/27 - seven - 创建。
 */
package com.seven.test.infrastructure.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @author seven
 */
@Slf4j
@Component
@Order(9)
public class LogFilter implements Filter {
  private static final String CONTENT_TYPE_FILE = "multipart/form-data";

  @Value("${request.log.path:/**}")
  private String[] path;

  private static PathMatcher matcher = new AntPathMatcher();

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    ContentCachingRequestWrapper wrapper = null;
    if (request instanceof HttpServletRequest && request.getContentType() != null && !request.getContentType().contains(CONTENT_TYPE_FILE)) {
      wrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
    }

    if (wrapper == null) {
      chain.doFilter(request, response);
      return;
    }

    String contextPath = wrapper.getRequestURI();
    boolean match = isMatchPath(contextPath);
    if (!match) {
      chain.doFilter(wrapper, response);
      return;
    }

    StringBuilder sb = new StringBuilder(contextPath);
    Map<String, String[]> parameterMap = wrapper.getParameterMap();
    parameterMap.forEach((k, v) -> {
      sb.append("?").append(k).append("=").append(v[0]);
    });
    // 最终拼好的路径
    String path = sb.toString();
    String requestBody = wrapper.getBody();
    requestBody = requestBody.replace("\\r", "");
    requestBody = requestBody.replace("\\n", "");
    log.info("URI : {}", path);
    log.info("Request body: {}", StringUtils.isEmpty(requestBody) ? "空" : requestBody);
    chain.doFilter(wrapper, response);

  }

  private boolean isMatchPath(String contextPath) {
    boolean match = false;
    for (String s : path) {
      if (!match) {
        match = matcher.match(s, contextPath);
      }
    }
    return match;
  }
}
