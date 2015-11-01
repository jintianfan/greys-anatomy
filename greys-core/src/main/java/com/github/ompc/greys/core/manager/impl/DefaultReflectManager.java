package com.github.ompc.greys.core.manager.impl;

import com.github.ompc.greys.core.manager.ReflectManager;
import com.github.ompc.greys.core.util.matcher.Matcher;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;

/**
 * 默认反射操作管理类实现
 * Created by vlinux on 15/11/1.
 */
public class DefaultReflectManager implements ReflectManager {

    private final ClassDataSource classDataSource;

    public DefaultReflectManager(ClassDataSource classDataSource) {
        this.classDataSource = classDataSource;
    }

    @Override
    public LinkedHashSet<Class<?>> searchClass(final Matcher<Class<?>> classMatcher) {
        final LinkedHashSet<Class<?>> classSet = new LinkedHashSet<Class<?>>();
        for (Class<?> clazz : classDataSource.allLoadedClasses()) {
            if (classMatcher.matching(clazz)) {
                classSet.add(clazz);
            }
        }
        return classSet;
    }

    @Override
    public LinkedHashSet<Class<?>> searchSubClass(final Class<?> targetClass) {
        final LinkedHashSet<Class<?>> classSet = new LinkedHashSet<Class<?>>();
        for (Class<?> clazz : classDataSource.allLoadedClasses()) {
            if (clazz.isAssignableFrom(targetClass)) {
                classSet.add(clazz);
            }
        }
        return classSet;
    }

    @Override
    public LinkedHashSet<Method> searchClassMethods(Class<?> targetClass, Matcher<Method> methodMatcher) {
        return null;
    }

}
