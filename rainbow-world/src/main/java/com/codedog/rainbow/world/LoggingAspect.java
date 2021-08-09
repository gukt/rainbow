/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.world;

import com.codedog.rainbow.core.CacheableObject;
import com.codedog.rainbow.core.CacheableObject.State;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * LoggingAspect class
 *
 * @author https://github.com/gukt
 */
@Component
@Aspect
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.codedog.rainbow.repository.*.save(..))")
    public void saveEntity() {}

    @Pointcut("execution(* com.codedog.rainbow.repository.*.delete(..))")
    public void deleteEntity() {}

    @Before("saveEntity()")
    public void deBefore1(JoinPoint joinPoint) throws Throwable {
        log.info("Saving: {}", joinPoint);
        // // 接收到请求，记录请求内容
        // ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // HttpServletRequest request = attributes.getRequest();
        // // 记录下请求内容
        // System.out.println("URL : " + request.getRequestURL().toString());
        // System.out.println("HTTP_METHOD : " + request.getMethod());
        // System.out.println("IP : " + request.getRemoteAddr());
        // System.out.println("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        // System.out.println("ARGS : " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(returning = "ret", pointcut = "saveEntity()")
    public void afterReturning(Object ret) throws Throwable {
        log.info("afterReturning: {}", ret);
        if (ret instanceof CacheableObject) {
            ((CacheableObject) ret).transitionState(State.UPDATE);
        }
    }

    @AfterThrowing("saveEntity()")
    public void afterThrowing(JoinPoint jp) {
        log.info("afterThrowing: {}", jp);
    }

    @After("saveEntity()")
    public void after(JoinPoint jp) {
        log.info("after: {}", jp);
    }

    @Around(value = "saveEntity()")
    public Object around(ProceedingJoinPoint pjp) {
        log.info("@Around - start");
        try {
            Object result = pjp.proceed();
            log.info("@Around - return: {}", result);
            return result;
        } catch (Throwable e) {
            log.error("@Around - throws", e);
            return null;
        }
    }

    // @Pointcut("execution(public * com.codedog.rainbow.world.controller.*.*(..))")
    // public void webLog() {}
    //
    // @Before("webLog()")
    // public void deBefore(JoinPoint joinPoint) throws Throwable {
    //     // 接收到请求，记录请求内容
    //     ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    //     HttpServletRequest request = attributes.getRequest();
    //     // 记录下请求内容
    //     System.out.println("URL : " + request.getRequestURL().toString());
    //     System.out.println("HTTP_METHOD : " + request.getMethod());
    //     System.out.println("IP : " + request.getRemoteAddr());
    //     System.out.println("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
    //     System.out.println("ARGS : " + Arrays.toString(joinPoint.getArgs()));
    //
    // }

    // @AfterReturning(returning = "ret", pointcut = "webLog()")
    // public void doAfterReturning(Object ret) throws Throwable {
    //     // 处理完请求，返回内容
    //     System.out.println("方法的返回值 : " + ret);
    // }
    //
    // //后置异常通知
    // @AfterThrowing("webLog()")
    // public void throwss(JoinPoint jp) {
    //     System.out.println("方法异常时执行.....");
    // }
    //
    // //后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
    // @After("webLog()")
    // public void after(JoinPoint jp) {
    //     System.out.println("方法最后执行.....");
    // }
    //
    // //环绕通知,环绕增强，相当于MethodInterceptor
    // @Around("webLog()")
    // public Object arround(ProceedingJoinPoint pjp) {
    //     System.out.println("方法环绕start.....");
    //     try {
    //         Object o = pjp.proceed();
    //         System.out.println("方法环绕proceed，结果是 :" + o);
    //         return o;
    //     } catch (Throwable e) {
    //         e.printStackTrace();
    //         return null;
    //     }
    // }
}
