package com.github.ompc.greys.core.util.matcher;

import java.util.Arrays;
import java.util.Collection;

/**
 * 组匹配
 * Created by vlinux on 15/11/1.
 */
public interface GroupMatching<T> extends Matcher<T> {

    /**
     * 与关系组匹配
     *
     * @param <T> 匹配类型
     */
    class And<T> implements GroupMatching<T> {

        private final Collection<Matcher<T>> matchers;

        /**
         * 与关系组匹配构造<br/>
         * 当且仅当目标符合匹配组的所有条件时才判定匹配成功
         *
         * @param matchers 待进行与关系组匹配的匹配集合
         */
        public And(Matcher<T>... matchers) {
            this.matchers = Arrays.asList(matchers);
        }

        @Override
        public boolean matching(T target) {
            for (Matcher<T> matcher : matchers) {
                if (!matcher.matching(target)) {
                    return false;
                }
            }
            return true;
        }

    }

    /**
     * 或关系组匹配
     *
     * @param <T> 匹配类型
     */
    class Or<T> implements GroupMatching<T> {

        private final Collection<Matcher<T>> matchers;

        /**
         * 或关系组匹配构造<br/>
         * 当且仅当目标符合匹配组的任一条件时就判定匹配成功
         *
         * @param matchers 待进行或关系组匹配的匹配集合
         */
        public Or(Matcher<T>... matchers) {
            this.matchers = Arrays.asList(matchers);
        }

        @Override
        public boolean matching(T target) {
            for (Matcher<T> matcher : matchers) {
                if (matcher.matching(target)) {
                    return true;
                }
            }
            return false;
        }
    }

}
