/*
 * Copyright 2018-2019 gukt, The Niuniu Project
 */

package com.codedog.rainbow.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author https://github.com/gukt
 * @date 2020/1/26 00:31
 * @version 1.0
 */
@JsonInclude(Include.NON_NULL)
@Data
@Accessors(fluent = true)
public class ApiResult {

  @JsonView(ApiResultView.class)
  private int code;

  @JsonView(ApiResultView.class)
  private int status = 200;

  @JsonView(ApiResultView.class)
  private String error;

  @JsonView(ApiResultView.class)
  private Object data;

  /** 默认成功 */
  public static final ApiResult OK = success("ok");

  /** 默认失败 */
  public static final ApiResult FAILED = failed(-1, "failed");

  public static ApiResult success(Object data) {
    return success(data, 200);
  }

  static ApiResult success(Object data, int status) {
    return of(0, null, data, status);
  }

  public static ApiResult failed(int code, String error) {
    return failed(code, error, 200);
  }

  static ApiResult failed(int code, String error, int status) {
    return of(code, error, null, status);
  }

  private static ApiResult of(int code, String error, Object data, int status) {
    ApiResult result = new ApiResult();
    result.code = code;
    result.data = data;
    result.error = error;
    result.status = status;
    return result;
  }
}
