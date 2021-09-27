/**
 * 项目名：	test-starter
 * 文件名：	UserController.java
 * 模块说明：
 * 修改历史：
 * 2021/9/27 - seven - 创建。
 */
package com.seven.test.application.controller;

import com.seven.test.infrastructure.logaspect.ApiAspectLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author seven
 */
@RestController
@RequestMapping(value = "/services/user")
public class UserController {

  @ApiAspectLog
  @GetMapping("/{id}")
  public String get(@PathVariable("id") String id) {
    return id;
  }

  @ApiAspectLog
  @GetMapping("/error/{id}")
  public String getError(@PathVariable("id") String id) throws Exception {
     throw new Exception(id);
  }
}
