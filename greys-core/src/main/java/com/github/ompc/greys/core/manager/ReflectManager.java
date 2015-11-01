package com.github.ompc.greys.core.manager;

import com.github.ompc.greys.core.manager.impl.DefaultReflectManager;
import com.github.ompc.greys.core.util.matcher.Matcher;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 反射操作管理类
 * Created by vlinux on 15/11/1.
 */
public interface ReflectManager {

    /**
     * 搜索类
     *
     * @param classMatcher 类匹配
     * @return 返回匹配的类集合
     */
    LinkedHashSet<Class<?>> searchClass(Matcher<Class<?>> classMatcher);

    /**
     * 搜索类
     *
     * @param classMatchers 类匹配集合(或关系)
     * @return 返回匹配的类集合
     */
    LinkedHashSet<Class<?>> searchClass(Collection<Matcher<Class<?>>> classMatchers);

    /**
     * 搜索目标类的所有子类
     *
     * @param targetClass 目标类
     * @return 返回匹配的类集合
     */
    LinkedHashSet<Class<?>> searchSubClass(Class<?> targetClass);

    /**
     * 搜索目标累集合中的所有子类
     *
     * @param targetClasses 目标类集合
     * @return 返回匹配的类集合
     */
    LinkedHashSet<Class<?>> searchSubClass(Set<Class<?>> targetClasses);

    /**
     * 搜索目标类的所有可见匹配方法
     *
     * @param targetClass   目标类
     * @param methodMatcher 方法匹配
     * @return 返回匹配的方法集合
     */
    LinkedHashSet<Method> searchClassMethods(Class<?> targetClass, Matcher<Method> methodMatcher);

    class Factory {

        private volatile static ReflectManager instance = null;

        public synchronized static ReflectManager initInstance(final Instrumentation inst) {
            if (null == instance) {
                instance = new DefaultReflectManager(inst);
            }
            return instance;
        }

        public synchronized static ReflectManager getInstance() {
            if (null == instance) {
                throw new IllegalStateException("not init yet.");
            }
            return instance;
        }
    }

}
