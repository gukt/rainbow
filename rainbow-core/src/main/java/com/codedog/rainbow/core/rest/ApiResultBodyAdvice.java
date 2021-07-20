/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core.rest;

import com.codedog.rainbow.core.Tag;
import com.codedog.rainbow.util.HttpUtils;
import com.codedog.rainbow.util.JsonUtils;
import com.codedog.rainbow.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * ApiResultBodyAdvice class
 *
 * @author https://github.com/gukt
 */
@RestControllerAdvice
@Slf4j
public class ApiResultBodyAdvice implements ResponseBodyAdvice<Object> {

    private final Map<String, Class<?>> viewTypesByName = new HashMap<>();

    @PostConstruct
    void init() {
        viewTypesByName.put("id-only", ApiResultView.IdOnly.class);
    }

    /**
     * 前置检查，以决定是否要执行 {@link #beforeBodyWrite} 方法。
     *
     * @param returnType    Controller 中 handler 方法返回值类型
     * @param converterType 如果使用 SpringBoot 的自动配置，这里的值是 MappingJackson2HttpMessageConverter
     * @return 如果希望某些返回值在 beforeBodyWrite 中统一处理则返回 true，反之false
     */
    @Override
    public boolean supports(MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        boolean isSwaggerUiResponse =
                returnType.getGenericParameterType().toString().contains("springfox");
        // 不要对 Swagger UI 请求进行拦截
        if (isSwaggerUiResponse) {
            return false;
        }
        // TODO 解决一下这里，用一个错误来测试
//        Method method = (Method) returnType.getExecutable();
//        boolean isBasisErrorHandler = "error".equals(method.getName())
//                && method.getDeclaringClass().equals(BasicErrorController.class);
//        return !isBasisErrorHandler;
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  @NotNull MethodParameter returnType,
                                  @NotNull MediaType selectedContentType,
                                  @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NotNull ServerHttpRequest request,
                                  @NotNull ServerHttpResponse response) {
        if (body instanceof MappingJacksonValue
                || body instanceof String
                || body instanceof Resource) {
            return body;
        }
        // 转换为自定义的分页（JsonPage），否则在应用 @JsonView 时，分页内容不能正常输出
        if (body instanceof Page<?>) {
            body = JsonPage.of((Page<?>) body);
        }
        // 从查询字符串中读取 view 参数值
        String viewName = HttpUtils.getParam(request, "view");
        // 使用指定的 view 过滤返回结果
        if (!ObjectUtils.isNullOrEmpty(viewName)) {
            body = setSerializationView(body, viewName);
            writeResponseLog(request, body);
            return body;
        }
        ApiResult result = body instanceof ApiResult
                ? (ApiResult) body
                : ApiResult.success(body);
        response.setStatusCode(HttpStatus.valueOf(result.status()));
        writeResponseLog(request, result);
        return result;
    }

    public void addApiResultViewClasses(Class<?>[] classes) {
        if (classes == null || classes.length == 0) {
            return;
        }
        Arrays.stream(classes)
                // 排除掉不是 ApiBasicView 类型的子类（或接口）
                .filter(ApiResultView.class::isAssignableFrom)
                .forEach(clazz -> {
                    Tag tag = clazz.getAnnotation(Tag.class);
                    if (tag != null) {
                        for (String name : tag.value()) {
                            viewTypesByName.put(name, clazz);
                        }
                    }
                });
    }

    /**
     * Handles {@link NoHandlerFoundException no-handle-found exceptions}
     * NOTE: 只有配置了 throw-exception-if-no-handler-found=true 才会抛出 {@link NoHandlerFoundException}
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleException(NoHandlerFoundException ex) {
        int status = HttpStatus.NOT_FOUND.value(); // 404
        return ApiResult.failed(status, ex.getMessage(), status);
    }

    /**
     * Handles {@link ApiException api exceptions}
     *
     * @param ex an {@link ApiException api exceptions}
     */
    @ExceptionHandler(ApiException.class)
    public Object handleException(ApiException ex) {
        return ex.toApiResult();
    }

    /**
     * Handles any unknown exceptions
     */
    @ExceptionHandler(Throwable.class)
    public Object handleException(Throwable ex) {
        log.error("处理请求时发生了未知异常", ex);
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value(); // 500
        return ApiResult.failed(status, ex.getMessage(), status);
    }

    // Private Methods

    private MappingJacksonValue setSerializationView(Object body, String viewName) {
        MappingJacksonValue jsonBody = !(body instanceof MappingJacksonValue)
                ? new MappingJacksonValue(body)
                : (MappingJacksonValue) body;
        Class<?> viewClass = viewTypesByName.get(viewName);
        if (viewClass != null) {
            jsonBody.setSerializationView(viewClass);
        }
        return jsonBody;
    }

    private void writeResponseLog(ServerHttpRequest request, Object result) {
        log.debug("{} - {} {} - {}", HttpUtils.getRemoteAddress(request),
                request.getMethodValue(),
                request.getURI().getPath(),
                JsonUtils.toJson(result));
    }
}
