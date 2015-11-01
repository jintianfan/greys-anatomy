package com.github.ompc.greys.core;

import com.github.ompc.greys.core.util.matcher.Matcher;

import java.lang.reflect.Method;

/**
 * 切入点
 * Created by vlinux on 15/10/24.
 */
public class PointCut {

    // 类匹配
    private final Matcher<Class<?>> classMatcher;

    // 方法匹配
    private final Matcher<Method> methodMatcher;

    /**
     * 构造切入点
     *
     * @param classMatcher  类匹配
     * @param methodMatcher 方法匹配
     */
    public PointCut(Matcher<Class<?>> classMatcher, Matcher<Method> methodMatcher) {
        this.classMatcher = classMatcher;
        this.methodMatcher = methodMatcher;
    }

    public Matcher<Class<?>> getClassMatcher() {
        return classMatcher;
    }

    public Matcher<Method> getMethodMatcher() {
        return methodMatcher;
    }
}
