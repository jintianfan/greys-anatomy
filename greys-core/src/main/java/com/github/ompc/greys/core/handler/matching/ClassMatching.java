package com.github.ompc.greys.core.handler.matching;

import com.github.ompc.greys.core.util.matcher.ClassMatcher;
import com.github.ompc.greys.core.util.matcher.Matcher;

/**
 * 类匹配
 * Created by vlinux on 15/11/2.
 */
public class ClassMatching {

    // 访问修饰符
    private int modifier;

    // 类类型
    private int type;

    // 类名匹配
    private PatternMatching classNameMatching;

    // 是否包含子类
    private boolean isIncludeSubClasses;

    // 是否包含属性
    private boolean isIncludeFields;

    // 是否包含ClassLoader
    private boolean isIncludeClassLoaders;

    // 是否包含接口
    private boolean isIncludeInterfaces;

    // 是否包含父类
    private boolean isIncludeSuperClasses;

    // 是否包含Annotation
    private boolean isIncludeAnnotations;

    public int getModifier() {
        return modifier;
    }

    public int getType() {
        return type;
    }

    public PatternMatching getClassNameMatching() {
        return classNameMatching;
    }

    public boolean isIncludeSubClasses() {
        return isIncludeSubClasses;
    }

    public boolean isIncludeFields() {
        return isIncludeFields;
    }

    public boolean isIncludeClassLoaders() {
        return isIncludeClassLoaders;
    }

    public boolean isIncludeInterfaces() {
        return isIncludeInterfaces;
    }

    public boolean isIncludeSuperClasses() {
        return isIncludeSuperClasses;
    }

    public boolean isIncludeAnnotations() {
        return isIncludeAnnotations;
    }

    private Matcher<String> toClassNameMatcher() {
        return null == classNameMatching
                ? null
                : classNameMatching.toMatcher();
    }

    /**
     * 转换为匹配器
     *
     * @return 对应匹配器
     */
    public Matcher<Class<?>> toMatcher() {
        return new ClassMatcher(modifier, type, toClassNameMatcher());
    }

}
