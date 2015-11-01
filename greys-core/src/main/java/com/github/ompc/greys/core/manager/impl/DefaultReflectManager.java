package com.github.ompc.greys.core.manager.impl;

import com.github.ompc.greys.core.manager.ReflectManager;
import com.github.ompc.greys.core.util.matcher.Matcher;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 默认反射操作管理类实现
 * Created by vlinux on 15/11/1.
 */
public class DefaultReflectManager implements ReflectManager {

    private final Instrumentation inst;

    public DefaultReflectManager(Instrumentation inst) {
        this.inst = inst;
    }

    /*
     * 获取所有已经被加载到JVM中的Class
     */
    private Collection<Class<?>> allLoadedClasses() {
        final Class<?>[] classArray = inst.getAllLoadedClasses();
        if (null == classArray) {
            return new ArrayList<Class<?>>();
        } else {
            return Arrays.asList(classArray);
        }
    }


    @Override
    public LinkedHashSet<Class<?>> searchClass(final Matcher<Class<?>> classMatcher) {
        final LinkedHashSet<Class<?>> classSet = new LinkedHashSet<Class<?>>();
        for (Class<?> clazz : allLoadedClasses()) {
            if (classMatcher.matching(clazz)) {
                classSet.add(clazz);
            }
        }
        return classSet;
    }

    @Override
    public LinkedHashSet<Class<?>> searchClass(final Collection<Matcher<Class<?>>> classMatchers) {
        final LinkedHashSet<Class<?>> classSet = new LinkedHashSet<Class<?>>();
        for (final Matcher<Class<?>> classMatcher : classMatchers) {
            classSet.addAll(searchClass(classMatcher));
        }
        return classSet;
    }

    @Override
    public LinkedHashSet<Class<?>> searchSubClass(final Class<?> targetClass) {
        final LinkedHashSet<Class<?>> classSet = new LinkedHashSet<Class<?>>();
        for (Class<?> clazz : allLoadedClasses()) {
            if (clazz.isAssignableFrom(targetClass)) {
                classSet.add(clazz);
            }
        }
        return classSet;
    }

    @Override
    public LinkedHashSet<Class<?>> searchSubClass(final Set<Class<?>> targetClasses) {
        final LinkedHashSet<Class<?>> classSet = new LinkedHashSet<Class<?>>();
        if (null == targetClasses) {
            return classSet;
        }
        for (final Class<?> targetClass : targetClasses) {
            classSet.addAll(searchSubClass(targetClass));
        }
        return classSet;
    }

    @Override
    public LinkedHashSet<Method> searchClassMethods(Class<?> targetClass, Matcher<Method> methodMatcher) {
        return null;
    }

}
