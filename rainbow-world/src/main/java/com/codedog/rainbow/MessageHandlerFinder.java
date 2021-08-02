/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */
package com.codedog.rainbow;

import com.codedog.rainbow.tcp.HandlerMapping;
import com.codedog.rainbow.tcp.MessageHandler;
import com.codedog.rainbow.tcp.MessageHandlerAdapter;
import com.codedog.rainbow.util.ObjectUtils;
import com.codedog.rainbow.util.ReflectionUtils;
import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 消息分发器，用以派发消息到指定的 {@link MessageHandler 消息处理器} 进行处理。
 *
 * @author https://github.com/gukt
 */
@Slf4j
public class MessageHandlerFinder {

    private final ApplicationContext context;

    public MessageHandlerFinder(ApplicationContext context) {
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public <T> List<MessageHandler<T>> findMessageHandlers(Class<T> expectedTypeArg) {
        log.debug("TCP - Scanning message handlers...");
        List<MessageHandler<T>> handlers = new ArrayList<>();
        Collection<?> candidates = context.getBeansWithAnnotation(Controller.class).values();
        for (Object bean : candidates) {
            Class<?> beanType = bean.getClass();
            // 是否实现了 MessageHandler 接口？
            if (MessageHandler.class.isAssignableFrom(beanType)) {
                // 类型参数是否匹配？
                if (ReflectionUtils.isTypeArgumentMatched(bean, expectedTypeArg)) {
                    handlers.add((MessageHandler<T>) bean);
                }
                continue;
            }
            MethodAccess methodAccess = MethodAccess.get(beanType);
            for (Method m : beanType.getDeclaredMethods()) {
                HandlerMapping mapping = m.getAnnotation(HandlerMapping.class);
                if (mapping != null) {
                    // 对于注解方法，使用“适配器模式“进行包装
                    handlers.add(MessageHandlerAdapter.of(bean, methodAccess, m.getName(), mapping.value()));
                }
            }
        }
        log.info("TCP - Finished message handler scanning: found {} handlers. {}", handlers.size(), toString(handlers));
        return handlers;
    }

    private String toString(List<?> handlers) {
        ObjectUtils.requireNonEmpty(handlers, "handlers");
        StringBuilder sb = new StringBuilder("\n");
        handlers.forEach(h -> sb.append(h.toString()).append("\n"));
        return sb.toString();
    }
}
