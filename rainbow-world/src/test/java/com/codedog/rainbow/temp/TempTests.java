/*
 * Copyright 2018-2021 codedog996.com, The rainbow Project.
 */

package com.codedog.rainbow.temp;

import com.codedog.rainbow.tcp.MessageHandler;
import com.codedog.rainbow.world.controller.EchoMessageHandler;
import io.netty.util.internal.TypeParameterMatcher;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * TempTests class
 *
 * @author https://github.com/gukt
 */
public class TempTests {

    @Test
    void test1() {
        EchoMessageHandler echoMessageHandler = new EchoMessageHandler();
        TypeParameterMatcher matcher = TypeParameterMatcher.find(echoMessageHandler, MessageHandler.class, "T");
        matcher.match(echoMessageHandler);

        Cat cat = new Cat();
        // class com.codedog.rainbow.temp.Cat
        System.out.println(cat.getClass());
        // class com.codedog.rainbow.temp.Animal
        System.out.println(cat.getClass().getSuperclass());
        // com.codedog.rainbow.temp.Animal<java.lang.String>
        System.out.println(cat.getClass().getGenericSuperclass());
        // []
        System.out.println(Arrays.toString(cat.getClass().getInterfaces()));
        // []
        System.out.println(Arrays.toString(cat.getClass().getGenericInterfaces()));
        // sun.reflect.annotation.AnnotatedTypeFactory$AnnotatedParameterizedTypeImpl@58129ba5
        System.out.println(cat.getClass().getAnnotatedSuperclass());

        Animal<String> dog1 = new Animal<String>(){};
        // 匿名对象实例
        // class com.codedog.rainbow.temp.TempTests$1
        System.out.println(dog1.getClass());

        Dog dog = new Dog();
        // class java.lang.Object
        System.out.println(dog.getClass().getSuperclass());
        // class java.lang.Object
        System.out.println(dog.getClass().getGenericSuperclass());
        // sun.reflect.annotation.AnnotatedTypeFactory$AnnotatedTypeBaseImpl@47f430a9
        System.out.println(dog.getClass().getAnnotatedSuperclass());
        // [com.codedog.rainbow.temp.Lifecycle<java.lang.String>]
        System.out.println(Arrays.toString(dog.getClass().getGenericInterfaces()));

        // 通过泛型接口获取 dog 对象的类型参数
        Type[] genericInterfaces = dog.getClass().getGenericInterfaces();
        if (genericInterfaces.length > 0){
           Type[] actualTypeArgs =  ((ParameterizedType)genericInterfaces[0]).getActualTypeArguments();
           if(actualTypeArgs.length > 0){
               System.out.println(actualTypeArgs[0]);
           }
        }

        WildDog<String> wildDog = new WildDog<>();
        List<WildDog<String>> children = wildDog.children();
        System.out.println(children);
        System.out.println(wildDog.getClass().getGenericSuperclass());

        WildDog<String> wildDog2 = new WildDog<>();
        System.out.println(wildDog2.getClass().getGenericSuperclass());

        WildDog<Object[]> wildDog3 = new WildDog<>();
        System.out.println(wildDog3.getClass().getGenericSuperclass());

        WildDog<List<String>[]> wildDog4 = new WildDog<>();
        System.out.println(wildDog4.getClass().getGenericSuperclass());

        WildDog<List<List<String[]>>[]> wildDog5 = new WildDog<>();
        System.out.println(wildDog5.getClass().getGenericSuperclass());
    }
}
