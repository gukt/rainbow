/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core.rest;

import com.codedog.rainbow.core.Tag;
import com.codedog.rainbow.util.HttpUtils;
import com.codedog.rainbow.util.JsonUtils;
import com.codedog.rainbow.util.ObjectUtils;
import com.fasterxml.jackson.annotation.JsonView;
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
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        Method method = (Method) returnType.getExecutable();
        return !Objects.equals("error", method.getName())
                && !method.getDeclaringClass().getSimpleName().contains("BasicErrorController");
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
        // 装饰成 ApiResultViewAwarePage 对象，否则在应用 @JsonView 时，分页内容不能正常输出，
        // 且该对象对分页内容的序列化格式做了优化。
        if (body instanceof Page<?>) {
            body = ApiResultViewAwarePage.of((Page<?>) body);
        }
        boolean isError = false;
        int status = 200;
        if (body instanceof ApiResult) {
            ApiResult result = (ApiResult) body;
            isError = result.code() != 0;
            status = result.status();
        } else {
            body = ApiResult.success(body);
        }
        // Set the HTTP status code of the response.
        response.setStatusCode(HttpStatus.valueOf(status));
        // 只有在成功的情况下才尝试应用请求参数中指定的 view
        if (!isError) {
            // 获得 handler 上是否指定了 JsonView
            JsonView jsonView = returnType.getMethodAnnotation(JsonView.class);
            // 如果 handler 上指定了 JsonView 注解, 则请求参数中指定 view 不生效
            if (jsonView == null) {
                // 从查询字符串中读取 view 参数值
                String viewName = HttpUtils.getParam(request, "view");
                if (!ObjectUtils.isNullOrEmpty(viewName)) {
                    // Wraps the body object to the MappingJacksonValue
                    body = resolveSerializationView2((ApiResult) body, viewName);
                }
            }
        }
        writeResponseLog(request, body);
        return body;
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
     * Handles {@link NoHandlerFoundException} globally
     * <p>
     * NOTE: 只有在 application[.properties|.yaml] 中配置了 'throw-exception-if-no-handler-found=true' 时，
     * 才会抛出 {@link NoHandlerFoundException}
     * </p>
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    Object handleException(NoHandlerFoundException ex) {
        log.error("处理请求时发生了未知异常", ex);
        int status = HttpStatus.NOT_FOUND.value(); // 404
        return ApiResult.failed(status, ex.getMessage(), status);
    }

    /**
     * Handles {@link ApiException} globally.
     *
     * @param ex an {@link ApiException api exceptions}
     */
    @ExceptionHandler(ApiException.class)
    Object handleException(ApiException ex) {
        log.error("处理请求时发生了未知异常", ex);
        return ex.toApiResult();
    }

    /**
     * Handles any unknown exceptions globally.
     */
    @ExceptionHandler(Throwable.class)
    Object handleException(Throwable ex) {
        log.error("处理请求时发生了未知异常", ex);
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value(); // 500
        return ApiResult.failed(status, ex.getMessage(), status);
    }

    // Private Methods

//    @Deprecated
//    private Object resolveSerializationView(Object body, String viewName) {
//        if (!(body instanceof ApiResult)) {
//            body = ApiResult.success(body);
//        }
//        Class<?> viewClass = viewTypesByName.get(viewName);
//        if (viewClass != null) {
//            body = new MappingJacksonValue(body);
//            ((MappingJacksonValue) body).setSerializationView(viewClass);
//        }
//        return body;
//    }

    private Object resolveSerializationView2(ApiResult result, String viewName) {
        Class<?> viewClass = viewTypesByName.get(viewName);
        if (viewClass == null) {
            return result;
        }
        MappingJacksonValue mappingResult = new MappingJacksonValue(result);
        mappingResult.setSerializationView(viewClass);
        return mappingResult;
    }

    /**
     * 记录 API 执行结果日志
     */
    private void writeResponseLog(ServerHttpRequest request, Object result) {
        log.debug("<<< {} - {} {} - {}", HttpUtils.getRemoteAddress(request),
                request.getMethodValue(),
                request.getURI().getPath(),
                JsonUtils.toJson(result));
    }
}
