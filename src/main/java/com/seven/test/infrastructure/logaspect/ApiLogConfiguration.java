/**
 * 项目名：	test-starter
 * 文件名：	ApiLogConfiguration.java
 * 模块说明：
 * 修改历史：
 * 2021/9/27 - seven - 创建。
 */
package com.seven.test.infrastructure.logaspect;

import com.seven.test.infrastructure.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author seven
 */
@Aspect
@Order(-9)
@Slf4j
@Component
@ConditionalOnProperty(value = "aspect-log.enabled", havingValue = "true", matchIfMissing = true)
public class ApiLogConfiguration {

  /** 请求前是否打印日志 */
  @Value("${aspect-log.before.enabled:true}")
  private boolean printBefore;
  /** 请求后是否打印日志 */
  @Value("${aspect-log.after.enabled:true}")
  private boolean printAfter;
  /** 请求异常是否打印日志 */
  @Value("${aspect-log.exception.enabled:true}")
  private boolean printException;

  @Pointcut("@annotation(com.seven.test.infrastructure.logaspect.ApiAspectLog) " +
          "||@within(com.seven.test.infrastructure.logaspect.ApiAspectLog)")
  public void restLog(){

  };

  @Before("restLog()")
  public void doBefore(JoinPoint joinPoint) {
    // 接收到请求，记录请求内容
    if (!printBefore) {
      return;
    }

    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes();

    if (attributes == null)
      return;

    HttpServletRequest request = attributes.getRequest();
    StringBuilder sb = new StringBuilder();
    sb.append("url: ").append(request.getRequestURL().toString()).append("\r\n");
    sb.append("querys: ").append(request.getQueryString()).append("\r\n");
    sb.append("charset: ").append(request.getCharacterEncoding()).append("\r\n");
    sb.append("method: ").append(request.getMethod()).append("\r\n");
    String headers = readRequestHeaders(request);
    sb.append("headers: ").append(headers).append("\r\n");

    for (Object obj : joinPoint.getArgs()) {
      if (obj == null || isBasicType(obj.getClass()) || obj instanceof HttpServletRequest
              || obj instanceof HttpServletResponse || obj instanceof MultipartFile) {
        continue;
      }
      sb.append("body: ").append(JsonUtil.objectToJson(obj)).append("\r\n");
      break;
    }
    sb.append("remoteAddr: ").append(request.getRemoteAddr()).append("\r\n");
    sb.append("client-ip: ").append(getIpAddress(request)).append("\r\n");
    log.info("收到请求:\n{}", sb.toString());
  }

  @AfterReturning(pointcut = "restLog()", returning = "retVal")
  public void doAfter(Object retVal) {
    if (!printAfter) {
      return;
    }
    log.info("返回结果:\n{}", JsonUtil.objectToJson(retVal));
  }

  @AfterThrowing(throwing = "ex", pointcut = "restLog()")
  public void doException(Exception ex) {
    if (!printException) {
      return;
    }
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes();
    if (attributes == null) {
      log.info("Error Request Result");
      return;
    }
    log.info("抛出异常", ex);
  }

  private static String getIpAddress(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    if (ip.contains(",")) {
      return ip.split(",")[0];
    } else {
      return ip;
    }
  }

  private static String readRequestHeaders(HttpServletRequest request) {
    Map<String, String> headers = new HashMap<>();

    Enumeration headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String key = (String) headerNames.nextElement();
      String value = request.getHeader(key);
      headers.put(key, value);
    }

    return headers.toString();
  }

  /**
   * 是否基本类型
   */
  public static boolean isBasicType(Class clazz) {
    if (clazz.isPrimitive()) {
      //基本类型
      return true;
    }
    return Number.class.isAssignableFrom(clazz)
            || clazz.isAssignableFrom(String.class)
            || clazz.isAssignableFrom(Boolean.class)
            || Date.class.isAssignableFrom(clazz)
            || Enum.class.isAssignableFrom(clazz);
  }


}
