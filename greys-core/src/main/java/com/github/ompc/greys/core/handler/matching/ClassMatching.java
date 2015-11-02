package com.github.ompc.greys.core.handler.matching;

import com.github.ompc.greys.core.util.matcher.ClassMatcher;
import com.github.ompc.greys.core.util.matcher.Matcher;

import static com.github.ompc.greys.core.util.GaReflectUtils.DEFAULT_MOD;
import static com.github.ompc.greys.core.util.GaReflectUtils.DEFAULT_TYPE;

/**
 * 类匹配
 * Created by vlinux on 15/11/2.
 */
public class ClassMatching {

    // 访问修饰符
    private int modifier = DEFAULT_MOD;

    // 类类型
    private int type = DEFAULT_TYPE;

    // 类名匹配
    private PatternMatching classNameMatching;

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
