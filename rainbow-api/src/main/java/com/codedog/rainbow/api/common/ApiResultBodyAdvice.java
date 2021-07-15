package com.codedog.rainbow.api.common;

import com.codedog.rainbow.util.Jsons;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codedog.rainbow.api.common.Https.getRemoteAddress;

/**
 * TODO 现在异常返回的不正确，可能和basePackages有关
 *
 * @author https://github.com/gukt
 * @date 2020/1/26 00:31
 * @version 1.0
 */
// @RestControllerAdvice = @ControllerAdvice + @ResponseBody
// @RestControllerAdvice(
//    basePackages = {
//      "com.example.rescuer",
//      "org.springframework.http.converter",
//      "org.springframework"
//    })
@RestControllerAdvice
@Slf4j
public class ApiResultBodyAdvice implements ResponseBodyAdvice<Object> {

  /**
   * 前置检查，以决定beforeBodyWrite方法是否要被执行
   *
   * @param returnType 表示Handler处理方法返回值的类型
   * @param converterType 如果使用SpringBoot的自动配置，这里的值是MappingJackson2HttpMessageConverter
   * @return 如果某些返回值希望在beforeBodyWrite中统一处理则返回true，反之false
   */
  @Override
  public boolean supports(
      MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
    // 由于这个全局的ResponseBodyAdvice对所有返回值都有影响，
    // 在引入Swagger UI后，也将它们的请求拦截掉了，因此这里简单的通过判断返回参数泛型类型中是否包含springfox来忽略这些请求
    boolean isSwaggerUiResponse =
        returnType.getGenericParameterType().toString().contains("springfox");
    if (isSwaggerUiResponse) {
      return false;
    }
    Method method = (Method) returnType.getExecutable();
    boolean isBasisErrorHandler =
        "error".equals(method.getName())
            && method.getDeclaringClass().equals(BasicErrorController.class);
    return !isBasisErrorHandler;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object beforeBodyWrite(
      Object body,
      @NonNull MethodParameter returnType,
      @NonNull MediaType selectedContentType,
      @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
      @NonNull ServerHttpRequest request,
      @NonNull ServerHttpResponse response) {
    //    log.debug("beforeBodyWrite: body: {}, selectedConverterType:{}", body,
    // selectedConverterType);

    // 如果handler中返回的是Resource或者包装过的JacksonValue，则直接返回不做处理
    if (body instanceof Resource || body instanceof String || body instanceof MappingJacksonValue) {
      return body;
    }
    if (body instanceof Page) {
      body = JsonPage.of((Page) body);
    }
    // 检查请求处理方法上有没有指定@JsonView
    JsonView anno = returnType.getMethodAnnotation(JsonView.class);
    // 从query字符串中解析请求地址中是否指定了view参数
    String viewName = getParameterValue(request.getURI().getQuery(), "view");
    // 有两种情况决定是否支持JsonView，第一种优先级最高会覆盖第二种
    // 1. 在handler方法上加上@JsonView注解，
    // 2. query里指定view参数
    boolean jsonViewSupported = anno != null || viewName != null;
    if (jsonViewSupported) {
      // 如果有view参数且是分页内容，则转换为自定义的分页，否则由于嵌套关系，应用@JsonView时会导致分页内容无法输出
      if (body instanceof Page) {
        body = JsonPage.of(((Page) body));
      }
      Object result = JsonViews.wrap(body, viewName);
      writeResponseLog(request, result);
      return result;
    }
    ApiResult result = body instanceof ApiResult ? (ApiResult) body : ApiResult.success(body);
    response.setStatusCode(HttpStatus.valueOf(result.status()));
    writeResponseLog(request, result);
    return result;
  }

  private void writeResponseLog(ServerHttpRequest request, Object result) {
    log.debug(
        "{} - {} {} - {}",
        getRemoteAddress(request),
        request.getMethodValue(),
        request.getURI().getPath(),
            Jsons.toJson(result));
  }

  /** 捕捉全局处理异常，将异常包装成统一的错误返回给客户端 */
  @ExceptionHandler(Throwable.class)
  public Object handleException(Exception e) {
    // 业务系统中显式检测的逻辑错误会以ApiException抛出
    if (e instanceof ApiException) {
      return ((ApiException) e).toApiResult();
    }
    if (e instanceof JsonViews.UnsupportedJsonView) {
      return ApiResult.failed(400, e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }
    // 如果请求的地址没有找到，则会抛出NoHandlerFoundException
    // NOTE: 必须在配置文件中设置throw-exception-if-no-handler-found=true，才会抛出NoHandlerFoundException
    // 否则，会返回SpringBoot自己包装的json数据返回，包含: timestamp, status, error, message, path
    else if (e instanceof NoHandlerFoundException) {
      return ApiResult.failed(404, e.getMessage(), HttpStatus.NOT_FOUND.value());
    } else {
      log.error("处理请求时发生异常", e);
      ApiResult error = Errors.ERR_HANDLE_EXCEPTION;
      error.status(HttpStatus.INTERNAL_SERVER_ERROR.value());
      error.error(e.getMessage());
      return error;
    }
  }

  /**
   * 从Query中查找指定名称参数的值
   *
   * @param query query String
   * @param param parameter name
   * @return parameter value
   */
  @Nullable
  private String getParameterValue(String query, String param) {
    if (query == null || param == null) {
      return null;
    }
    Pattern regex = Pattern.compile(".*" + param + "=([^&]+)&?.*");
    Matcher m = regex.matcher(query);
    return m.find() ? m.group(1) : null;
  }
}
