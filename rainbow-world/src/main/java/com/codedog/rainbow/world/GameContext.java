/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.world;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Created by gukt <gukaitong@gmail.com> on 2019-07-05 02:41
 *
 * @author gukt <gukaitong@gmail.com>
 */
@Component
public class GameContext {

    @Getter
    @Setter
    private GameApp app;
    @Getter
    @Setter
    private GameOptions options;
    @Getter
    @Setter
    private EventPublisher eventPublisher;
    @Getter
    @Setter
    private TemplateManager templateManager;

    // public EventPublisher getEventPublisher() {
    //     return app.getEventPublisher();
    // }
    //
    // public TemplateManager getTemplateManager() {
    //     return app.getTemplateManager();
    // }

    // /**
    //  * 私有构造函数，阻止使用new来构造对象实例，使用getInstance()方法获得该类型的单例对象
    //  */
    // private GameContext() {
    //     // throw new AssertionError("No GameContext instances for you.");
    // }

    // public static GameContext getInstance() {
    //     return GameContextHolder.INSTANCE;
    // }
    //
    // /**
    //  * 使用Demand Holder模式实现线程安全的单例
    //  * 这比"double-checked locking"模式更高效，且更容易理解
    //  * 这种单例实现模式是有由"内部类的静态成员直到第一次使用时才初始化"的特性保证的
    //  */
    // private static class GameContextHolder {
    //
    //     private static final GameContext INSTANCE = new GameContext();
    // }
}
