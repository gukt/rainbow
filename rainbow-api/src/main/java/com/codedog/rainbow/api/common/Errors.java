package com.codedog.rainbow.api.common;


import com.codedog.rainbow.core.rest.ApiResult;

import static com.codedog.rainbow.core.rest.ApiResult.failed;

/**
 * @author https://github.com/gukt
 * @date 2020/1/26 19:18
 * @version 1.0
 */
public class Errors {

  public static final ApiResult ERR_HANDLE_EXCEPTION = failed(10000, "处理请求异常");
  public static final ApiResult ERR_ENTITY_NOT_FOUND = failed(10001, "记录没找到");
  public static final ApiResult ERR_ENTITY_EXISTS = failed(10002, "记录已经存在");
  public static final ApiResult ERR_BAD_PARAMETER = failed(10003, "请求参数有问题");
  public static final ApiResult ERR_ILLEGAL_REQUEST = failed(10004, "非法的请求");

  public static final ApiResult ERR_REQUIRE_ADMIN = failed(20000, "需要管理员权限");
  public static final ApiResult ERR_REQUIRE_LOGIN = failed(20001, "需要登录");
  public static final ApiResult ERR_INVALID_NAME_OR_PASSWORD = failed(20002, "用户名或密码不正确");
  public static final ApiResult ERR_ALREADY_LOGIN = failed(20003, "已经登陆了");
  public static final ApiResult ERR_INVALID_CAPTCHA = failed(20004, "验证码不正确");
  public static final ApiResult ERR_INVALID_OLD_PASSWORD = failed(20005, "旧密码不正确");

  public static ApiResult of(int code, String error) {
    return ApiResult.failed(code, error);
  }
}
