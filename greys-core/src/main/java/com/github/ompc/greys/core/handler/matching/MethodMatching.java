package com.github.ompc.greys.core.handler.matching;

import java.util.Collection;

import static com.github.ompc.greys.core.util.GaReflectUtils.DEFAULT_MOD;

/**
 * 方法匹配
 * Created by vlinux on 15/11/2.
 */
public class MethodMatching {

    // 访问修饰符
    private int modifier = DEFAULT_MOD;

    // 方法名匹配
    private PatternMatching name;

    // 参数数组类型匹配
    private Collection<ClassMatching> parameters;

    public int getModifier() {
        return modifier;
    }

    public PatternMatching getName() {
        return name;
    }

    public Collection<ClassMatching> getParameters() {
        return parameters;
    }
}
