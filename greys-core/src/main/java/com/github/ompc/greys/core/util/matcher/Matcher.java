package com.github.ompc.greys.core.util.matcher;

/**
 * 匹配器
 * Created by vlinux on 15/10/24.
 */
public interface Matcher<T> {

    /**
     * 是否匹配
     *
     * @param target 目标
     * @return 目标是否匹配
     */
    boolean matching(T target);

}
