/*
 * Copyright 2018-2019 laogu.io, The rainbow Project
 */

package com.codedog.rainbow.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 抄来的，提供类查找相关工具方法
 * TODO 找一个更优秀的ClassUtils实现类
 */
@Slf4j
public class ClassUtils {

    public static Set<Class<?>> getClasses(String packageScan) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        String packageDir = packageScan.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDir);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String proto = url.getProtocol();
                if ("file".equals(proto)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageScan, filePath, true, classes);
                } else if ("jar".equals(proto)) {
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    classes.addAll(findClassesInJar(jar, packageDir, packageScan));
                }
            }
        } catch (IOException e) {
            log.error("An error occurred: ", e);
        }

        return classes;
    }

    private static Set<Class<?>> findClassesInJar(JarFile jar, String packageDir, String packageScan) {
        Set<Class<?>> classes = new HashSet<>();
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }
            if (name.startsWith(packageDir)) {
                int idx = name.lastIndexOf('/');
                if (idx != -1) {
                    packageScan = name.substring(0, idx).replace('/', '.');
                }
                if (name.endsWith(".class") && !entry.isDirectory()) {
                    String className = name.substring(packageScan.length() + 1, name.length() - 6);
                    try {
                        classes.add(Class.forName(packageScan + '.' + className));
                    } catch (ClassNotFoundException e) {
                        log.error("Cannot find class: {}", e.getMessage());
                    }
                }
            }
        }
        return classes;
    }

    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
            final boolean recursive, Set<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirfiles = dir.listFiles(file -> (recursive && file.isDirectory())
                || (file.getName().endsWith(".class")));

        if (dirfiles != null && dirfiles.length > 0) {
            for (File file : dirfiles) {
                if (file.isDirectory()) {
                    findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                            file.getAbsolutePath(), recursive, classes);
                } else {
                    // 如果是java类文件 去掉后面的.class 只留下类名
                    String className = file.getName().substring(0,
                            file.getName().length() - 6);
                    try {
                        // 添加到集合中去
                        //classes.add(Class.forName(packageName + '.' + className));
                        //经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                        classes.add(Thread.currentThread().getContextClassLoader()
                                .loadClass(packageName + '.' + className));
                    } catch (ClassNotFoundException e) {
                        // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                        log.error("Exception：", e);
                    }
                }
            }
        }
    }
}
