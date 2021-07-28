/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */
package com.codedog.rainbow.tcp;

import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息分发器，用以派发消息到指定的 {@link MessageHandler 消息处理器} 进行处理。
 *
 * @author https://github.com/gukt
 */
@Component
@Slf4j
public class MessageHandlerFinder {

    private final ApplicationContext context;

    public MessageHandlerFinder(ApplicationContext context) {this.context = context;}

    public <T> List<MessageHandler<T>> findMessageHandlers(Class<T> targetType) {
        log.debug("TCP - Scanning message handlers...");
        List<MessageHandler<T>> handlers = new ArrayList<>();
        for (Object bean : context.getBeansWithAnnotation(Controller.class).values()) {
            Class<?> beanType = bean.getClass();
            // TODO 要检查 MessageHandler 的参数类型是否匹配
            // 添加实现了 MessageHandler 接口的 Beans
            if (MessageHandler.class.isAssignableFrom(beanType)) {
                handlers.add((MessageHandler<T>) bean);
                continue;
            }
            MethodAccess methodAccess = MethodAccess.get(beanType);

            for (Method m : beanType.getDeclaredMethods()) {
                HandlerMapping mapping = m.getAnnotation(HandlerMapping.class);
                if (mapping != null) {
                    // 对于注解方法，使用“适配器模式”将方法包装一下
                    handlers.add(new MessageHandlerAdapter<T>(bean, methodAccess, m.getName()) {
                        @Override
                        public Serializable getType() {
                            return mapping.value();
                        }
                    });
                }
            }
        }
        log.info("TCP - Finished message handler scanning: found {} handlers.", handlers.size());
        return handlers;
    }
}
