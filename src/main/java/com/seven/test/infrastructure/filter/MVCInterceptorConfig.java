/**
 * 项目名：	test-starter
 * 文件名：	MVCInterceptorConfig.java
 * 模块说明：
 * 修改历史：
 * 2021/9/27 - seven - 创建。
 */
package com.seven.test.infrastructure.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author seven
 */
@Configuration
public class MVCInterceptorConfig implements WebMvcConfigurer {

  @Value("${request.log.path:[]}")
  private String[]  path;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LogInterceptor()).addPathPatterns(path);
    WebMvcConfigurer.super.addInterceptors(registry);
  }
}
