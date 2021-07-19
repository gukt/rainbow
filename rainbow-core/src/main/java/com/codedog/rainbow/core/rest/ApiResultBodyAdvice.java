/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.core.rest;

import com.codedog.rainbow.core.Tag;
import com.codedog.rainbow.util.Https;
import com.codedog.rainbow.util.Jsons;
import com.codedog.rainbow.util.MoreObjects;
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
        String viewName = Https.getParam(request, "view");
        // 使用指定的 view 过滤返回结果
        if (!MoreObjects.isNullOrEmpty(viewName)) {
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

    private final Map<String, Class<?>> viewTypesByName = new HashMap<>();

    public void addViewClasses(Class<?>[] classes) {
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

    private MappingJacksonValue setSerializationView(Object body, String viewName) {
//        if (!(body instanceof ApiResult)) {
//            body = ApiResult.success(body);
//        }
        MappingJacksonValue bodyContainer = new MappingJacksonValue(body);
        Class<?> viewClass = viewTypesByName.get(viewName);
        if (viewClass != null) {
            bodyContainer.setSerializationView(viewClass);
        }
        return bodyContainer;
    }

//    /** 捕捉全局处理异常，将异常包装成统一的错误返回给客户端 */
//    @ExceptionHandler(Throwable.class)
//    public Object handleException(Exception e) {
////        // 业务系统中显式检测的逻辑错误会以ApiException抛出
////        if (e instanceof ApiException) {
////            return ((ApiException) e).toApiResult();
////        }
////        if (e instanceof BadJsonViewException) {
////            return ApiResult.failed(400, e.getMessage(), HttpStatus.BAD_REQUEST.value());
////        }
//        // TODO 解决一下这里，紧急
////        // 如果请求的地址没有找到，则会抛出NoHandlerFoundException
////        // NOTE: 必须在配置文件中设置throw-exception-if-no-handler-found=true，才会抛出NoHandlerFoundException
////        // 否则，会返回SpringBoot自己包装的json数据返回，包含: timestamp, status, error, message, path
////        else if (e instanceof NoHandlerFoundException) {
////            return ApiResult.failed(404, e.getMessage(), HttpStatus.NOT_FOUND.value());
////        } else {
////            log.error("处理请求时发生异常", e);
////            ApiResult error = ApiResult.failed(10000, "处理请求异常");
////            error.status(HttpStatus.INTERNAL_SERVER_ERROR.value());
////            error.error("处理请求异常");
////            return error;
////        }
//        log.error("处理请求时发生异常", e);
//        ApiResult error = ApiResult.failed(10000, "处理请求异常");
//        error.status(HttpStatus.INTERNAL_SERVER_ERROR.value());
//        error.error("处理请求异常");
//        return error;
//    }

    /**
     * 处理 {link NoHandlerFoundException}
     * NOTE: 只有配置了 throw-exception-if-no-handler-found=true，才会抛出 {@link NoHandlerFoundException}
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleException(NoHandlerFoundException ex) {
        int status = HttpStatus.NOT_FOUND.value(); // 404
        return ApiResult.failed(status, ex.getMessage(), status);
    }

    @ExceptionHandler(ViewNotFoundException.class)
    public Object handleException(ViewNotFoundException ex) {
        int status = HttpStatus.BAD_REQUEST.value(); // 400
        return ApiResult.failed(status, ex.getMessage(), status);
    }

    /**
     * 处理 {@link ApiException}，业务系统中显式检测的逻辑错误会以 {@link ApiException} 抛出。
     */
    @ExceptionHandler(ApiException.class)
    public Object handleException(ApiException ex) {
        return ex.toApiResult();
    }

    @ExceptionHandler(Throwable.class)
    public Object handleException(Throwable e) {
        log.error("处理请求时发生了未知异常", e);
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value(); // 500
        return ApiResult.failed(status, "处理请求时发生了未知异常", status);
    }

//    private static final Map<String, Class<? extends ApiResultView>> viewClassesByName = new HashMap<>();
//    private static Class<? extends ApiResultView> getJsonViewClass(String viewName) {
//        if (viewName == null) {
//            return null;
//        }
//        Class<? extends ApiResultView> viewClass = viewClassesByName.get(viewName);
//        if (viewClass == null) {
//            String packageName = JsonViews.class.getName();
//            String className = packageName + "$" + viewName;
//            try {
//                viewClass = (Class<ApiResultView>) Class.forName(className);
//                JsonViewNameAlias alias = viewClass.getAnnotation(JsonViewNameAlias.class);
//                if (alias != null) {
//                    for (String v : alias.value()) {
//                        viewClassesByName.put(v, viewClass);
//                    }
//                }
//                viewClassesByName.put(viewName, viewClass);
//            } catch (ClassNotFoundException e) {
//                throw new BadJsonViewException(viewName);
//            }
//        }
//        return viewClass;
//    }

    private void writeResponseLog(ServerHttpRequest request, Object result) {
        log.debug("{} - {} {} - {}", Https.getRemoteAddress(request),
                request.getMethodValue(),
                request.getURI().getPath(),
                Jsons.toJson(result));
    }
}
