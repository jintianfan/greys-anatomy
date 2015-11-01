package com.github.ompc.greys.core.util.matcher;

import java.lang.annotation.Annotation;
import java.util.Collection;

import static com.github.ompc.greys.core.util.GaReflectUtils.*;

/**
 * 类匹配
 * Created by vlinux on 15/10/31.
 */
public class ClassMatcher extends ReflectMatcher<Class<?>> {

    // 类型
    private final int type;

    /**
     * 构造类匹配
     *
     * @param modifier    访问修饰符枚举，参考 {@link ReflectMatcher}
     * @param type        类型枚举，参考 {@link com.github.ompc.greys.core.util.GaReflectUtils}
     * @param name        类名匹配
     * @param annotations 直接修饰类的Annotation匹配器
     */
    public ClassMatcher(
            int modifier,
            int type,
            Matcher<String> name,
            Collection<Matcher<Class<? extends Annotation>>> annotations) {
        super(modifier, name, annotations);
        this.type = type;
    }


    public ClassMatcher(int modifier, int type, Matcher<String> name) {
        this(modifier, type, name, null);
    }

    @Override
    public boolean reflectMatching(Class<?> target) {

        // 匹配type
        if (!matchingType(target)) {
            return false;
        }

        return true;
    }

    @Override
    int getTargetModifiers(Class<?> target) {
        return computeClassModifier(target);
    }

    @Override
    String getTargetName(Class<?> target) {
        return target.getName();
    }

    @Override
    Annotation[] getTargetAnnotationArray(Class<?> target) {
        return target.getAnnotations();
    }

    private boolean matchingType(Class<?> targetClass) {
        // 如果默认就是全类型，就不用比了
        if (type == DEFAULT_TYPE) {
            return true;
        }
        return (type & computeClassType(targetClass)) != 0;
    }


}
