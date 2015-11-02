package com.github.ompc.greys.core.manager.impl;

import com.github.ompc.greys.core.GaMethod;
import com.github.ompc.greys.core.manager.ReflectManager;
import com.github.ompc.greys.core.util.GaReflectUtils;
import com.github.ompc.greys.core.util.matcher.Matcher;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 默认反射操作管理类实现
 * Created by vlinux on 15/11/1.
 */
public class DefaultReflectManager implements ReflectManager {

    private final ClassDataSource classDataSource;

    public DefaultReflectManager(ClassDataSource classDataSource) {
        this.classDataSource = classDataSource;
    }

    @Override
    public Collection<Class<?>> searchClass(final Matcher<Class<?>> classMatcher) {
        final Set<Class<?>> classSet = new LinkedHashSet<Class<?>>();
        for (Class<?> clazz : classDataSource.allLoadedClasses()) {
            if (classMatcher.matching(clazz)) {
                classSet.add(clazz);
            }
        }
        return classSet;
    }

    @Override
    public Collection<Class<?>> searchSubClass(final Class<?> targetClass) {
        final Set<Class<?>> classSet = new LinkedHashSet<Class<?>>();
        for (Class<?> clazz : classDataSource.allLoadedClasses()) {
            if (clazz.isAssignableFrom(targetClass)) {
                classSet.add(clazz);
            }
        }
        return classSet;
    }


    /**
     * 返回类中的所有可见方法<br/>
     * 所谓可见方法的定义是开发在类中可以直接通过Java语法继承关系感知到的方法
     *
     * @param clazz 目标类
     * @return 类的所有可见方法
     */
    private Set<Method> listVisualMethod(final Class<?> clazz) {
        final Set<Method> methodSet = new LinkedHashSet<Method>();

        // 首先查出当前类所声明的所有方法
        final Method[] classDeclaredMethodArray = clazz.getDeclaredMethods();
        if (null != classDeclaredMethodArray) {
            for (Method declaredMethod : classDeclaredMethodArray) {
                methodSet.add(declaredMethod);
            }
        }

        // 查出当前类所有的父类
        final Collection<Class<?>> superClassSet = GaReflectUtils.recGetSuperClass(clazz);

        // 查出所有父类的可见方法
        for (Class<?> superClass : superClassSet) {
            final Method[] superClassDeclaredMethodArray = superClass.getDeclaredMethods();
            if (null != superClassDeclaredMethodArray) {
                for (Method superClassDeclaredMethod : superClassDeclaredMethodArray) {

                    final int modifier = superClassDeclaredMethod.getModifiers();

                    // 私有方法可以过滤掉
                    if (Modifier.isPrivate(modifier)) {
                        continue;
                    }

                    // public & protected 这两种情况是可以通过继承可见
                    // 所以放行
                    else if (Modifier.isPublic(modifier)
                            || Modifier.isProtected(modifier)) {
                        methodSet.add(superClassDeclaredMethod);
                    }

                    // 剩下的情况只剩下默认, 默认的范围需要同包才能生效
                    else if (clazz.getPackage().equals(superClassDeclaredMethod.getDeclaringClass().getPackage())) {
                        methodSet.add(superClassDeclaredMethod);
                    }

                }
            }
        }

        return methodSet;
    }

    @Override
    public Collection<GaMethod> searchClassGaMethods(Class<?> targetClass, Matcher<GaMethod> gaMethodMatcher) {

        final Set<Method> visualMethodSet = listVisualMethod(targetClass);
        final Set<GaMethod> gaMethodSet = new LinkedHashSet<GaMethod>();

        for (final Method method : visualMethodSet) {
            final GaMethod gaMethod = new GaMethod.MethodImpl(method);
            if (gaMethodMatcher.matching(gaMethod)) {
                gaMethodSet.add(gaMethod);
            }
        }

        return gaMethodSet;
    }

}
