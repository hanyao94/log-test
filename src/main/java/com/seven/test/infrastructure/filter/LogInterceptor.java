/**
 * 项目名：	test-starter
 * 文件名：	LogInterceptor.java
 * 模块说明：
 * 修改历史：
 * 2021/9/27 - seven - 创建。
 */
package com.seven.test.infrastructure.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author seven
 */
@Slf4j
public class LogInterceptor implements HandlerInterceptor {
  public static final String CONTENT_TYPE_FILE = "multipart/form-data";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if (request.getContentType() == null || request.getContentType().contains(CONTENT_TYPE_FILE)) {
      return true;
    }
    ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(request);
    String requestBody = wrapper.getBody();
    String servletPath = wrapper.getServletPath();
    StringBuilder sb = new StringBuilder(servletPath);
    Map<String, String[]> parameterMap = wrapper.getParameterMap();
    parameterMap.forEach((k, v) -> {
      sb.append("?").append(k).append("=").append(v[0]);
    });
    // 最终拼好的路径
    String path = sb.toString();
    // 请求体 @RequestBody
    requestBody = requestBody.replace("\\r", "");
    requestBody = requestBody.replace("\\n", "");
    log.info("URI : {}", path);
    log.info("Request body: {}", StringUtils.isEmpty(requestBody) ? "空" : requestBody);
    return true;
  }
}
