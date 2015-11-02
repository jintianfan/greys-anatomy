package com.github.ompc.greys.core.handler.impl;

import com.github.ompc.greys.core.GaMethod;
import com.github.ompc.greys.core.handler.matching.ClassMatching;
import com.github.ompc.greys.core.handler.matching.MethodMatching;
import com.github.ompc.greys.core.handler.matching.PatternMatching;
import com.github.ompc.greys.core.manager.ReflectManager;
import com.github.ompc.greys.core.util.matcher.ClassMatcher;
import com.github.ompc.greys.core.util.matcher.GaMethodMatcher;
import com.github.ompc.greys.core.util.matcher.Matcher;
import com.github.ompc.greys.core.util.matcher.PatternMatcher;

import java.util.*;

/**
 * 匹配支持
 * Created by vlinux on 15/11/2.
 */
public abstract class MatchingSupport {

    private final ReflectManager reflectManager = ReflectManager.Factory.getInstance();

    final protected Collection<Class<?>> matchingClasses(
            final ClassMatching classMatching, final boolean isIncludeSubClasses) {
        final Set<Class<?>> classSet = new LinkedHashSet<Class<?>>();
        if (null == classMatching) {
            return classSet;
        }

        // 搜索所有匹配器需求
        // 搜索当前匹配器所匹配的类
        final Collection<Class<?>> matchedClasses = reflectManager.searchClass(toMatcher(classMatching));
        classSet.addAll(matchedClasses);

        // 如果要求搜索子类，则需要继续添加
        if (isIncludeSubClasses) {
            for (final Class<?> matchedClass : matchedClasses) {
                classSet.addAll(reflectManager.searchSubClass(matchedClass));
            }
        }

        return classSet;
    }


    final protected Collection<GaMethod> matchingGaMethod(
            final ClassMatching classMatching,
            final boolean isIncludeSubClasses,
            final MethodMatching methodMatching) {


        final Matcher<GaMethod> gaMethodMatcher = toMatcher(methodMatching);
        final Set<GaMethod> matchedMethodSet = new LinkedHashSet<GaMethod>();

        final Collection<Class<?>> matchedClassSet = matchingClasses(classMatching, isIncludeSubClasses);
        for (Class<?> matchedClass : matchedClassSet) {
            matchedMethodSet.addAll(reflectManager.searchClassGaMethods(matchedClass, gaMethodMatcher));
        }

        return matchedMethodSet;
    }

    /**
     * 转换为模式匹配器
     *
     * @param patternMatching 模式匹配
     * @return 模式匹配器
     */
    private Matcher<String> toMatcher(final PatternMatching patternMatching) {
        return new PatternMatcher(
                patternMatching.getType(),
                patternMatching.getPattern()
        );
    }

    /**
     * 转换为类匹配器
     *
     * @param classMatching 类匹配
     * @return 类匹配器
     */
    private Matcher<Class<?>> toMatcher(final ClassMatching classMatching) {
        return new ClassMatcher(
                classMatching.getModifier(),
                classMatching.getType(),
                toMatcher(classMatching.getName())
        );
    }

    private List<Matcher<Class<?>>> toMatchers(final Collection<ClassMatching> classMatchingList) {
        if (null == classMatchingList) {
            return null;
        }
        final List<Matcher<Class<?>>> classMatcherList = new ArrayList<Matcher<Class<?>>>();
        for (ClassMatching classMatching : classMatchingList) {
            classMatcherList.add(toMatcher(classMatching));
        }
        return classMatcherList;
    }

    /**
     * 转换为方法匹配器
     *
     * @param methodMatching 方法匹配
     * @return 方法匹配器
     */
    private Matcher<GaMethod> toMatcher(final MethodMatching methodMatching) {
        return new GaMethodMatcher(
                methodMatching.getModifier(),
                toMatcher(methodMatching.getName()),
                toMatchers(methodMatching.getParameters()),
                null
        );
    }

}
