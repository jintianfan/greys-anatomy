package com.github.ompc.greys.core.util.matcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import static com.github.ompc.greys.core.util.GaReflectUtils.computeMethodModifier;

/**
 * 方法匹配
 * Created by vlinux on 15/11/1.
 */
public class MethodMatcher extends ReflectMatcher<Method> {

    // 方法参数匹配(顺序相关)
    private final Matcher<Class<?>> parameterClassMatchers[];

    public MethodMatcher(
            int modifier,
            Matcher<String> name,
            Matcher<Class<?>>[] parameterClassMatchers,
            Collection<Matcher<Class<? extends Annotation>>> annotations) {
        super(modifier, name, annotations);
        this.parameterClassMatchers = parameterClassMatchers;
    }

    @Override
    boolean reflectMatching(Method targetMethod) {
        if (!matchingParameters(targetMethod)) {
            return false;
        }
        return true;
    }

    private boolean matchingParameters(final Method targetMethod) {

        final Class<?>[] targetParameterClassArray = targetMethod.getParameterTypes();

        // 推空保护
        if (null == parameterClassMatchers
                || null == targetParameterClassArray) {
            return true;
        }


        if (targetParameterClassArray.length != parameterClassMatchers.length) {
            return false;
        }

        final int length = targetParameterClassArray.length;
        for (int index = 0; index < length; index++) {
            final Matcher<Class<?>> classMatcher = parameterClassMatchers[index];
            if (!classMatcher.matching(targetParameterClassArray[index])) {
                return false;
            }
        }
        return true;
    }

    @Override
    int getTargetModifiers(Method target) {
        return computeMethodModifier(target);
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
