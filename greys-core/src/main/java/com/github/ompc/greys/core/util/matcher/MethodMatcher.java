package com.github.ompc.greys.core.util.matcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 方法匹配
 * Created by vlinux on 15/11/1.
 */
public class MethodMatcher extends ReflectMatcher<Method> {

    // 方法参数匹配(顺序相关)
    private final Matcher<Class<?>> parameterArray[];

    public MethodMatcher(
            int modifier,
            Matcher<String> name,
            Matcher<Class<?>>[] parameterArray,
            Collection<Matcher<Class<? extends Annotation>>> annotations) {
        super(modifier, name, annotations);
        this.parameterArray = parameterArray;
    }

    @Override
    boolean reflectMatching(Method target) {
        return false;
    }

    @Override
    int getTargetModifiers(Method target) {
        return target.getModifiers();
    }

    @Override
    String getTargetName(Method target) {
        return target.getName();
    }

    @Override
    Annotation[] getTargetAnnotationArray(Method target) {
        return target.getAnnotations();
    }
}
