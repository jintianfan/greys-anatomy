package com.github.ompc.greys.core.handler.matching;

import com.github.ompc.greys.core.util.matcher.Matcher;
import com.github.ompc.greys.core.util.matcher.PatternMatcher;

/**
 * 模式匹配
 * Created by vlinux on 15/11/2.
 */
public class PatternMatching {

    // 模式字符串
    private String pattern;

    // 模式类型
    private int type;

    /**
     * 转换为匹配器
     *
     * @return 对应匹配器
     */
    public Matcher<String> toMatcher() {
        return new PatternMatcher(type, pattern);
    }

}
